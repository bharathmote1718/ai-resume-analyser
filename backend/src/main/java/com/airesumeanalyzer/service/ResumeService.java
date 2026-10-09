package com.airesumeanalyzer.service;

import com.airesumeanalyzer.dto.AnalysisResultDto;
import com.airesumeanalyzer.dto.JobMatchResponse;
import com.airesumeanalyzer.dto.ResumeResponse;
import com.airesumeanalyzer.entity.JobDescription;
import com.airesumeanalyzer.entity.JobMatchAnalysis;
import com.airesumeanalyzer.entity.Resume;
import com.airesumeanalyzer.entity.ResumeAnalysis;
import com.airesumeanalyzer.entity.User;
import com.airesumeanalyzer.exception.ResourceNotFoundException;
import com.airesumeanalyzer.repository.JobDescriptionRepository;
import com.airesumeanalyzer.repository.JobMatchAnalysisRepository;
import com.airesumeanalyzer.repository.ResumeAnalysisRepository;
import com.airesumeanalyzer.repository.ResumeRepository;
import com.airesumeanalyzer.repository.UserRepository;
import com.airesumeanalyzer.service.ai.AiAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobMatchAnalysisRepository jobMatchAnalysisRepository;
    private final TextExtractionService textExtractionService;
    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeService(
        ResumeRepository resumeRepository,
        UserRepository userRepository,
        ResumeAnalysisRepository resumeAnalysisRepository,
        JobDescriptionRepository jobDescriptionRepository,
        JobMatchAnalysisRepository jobMatchAnalysisRepository,
        TextExtractionService textExtractionService,
        AiAnalysisService aiAnalysisService
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobMatchAnalysisRepository = jobMatchAnalysisRepository;
        this.textExtractionService = textExtractionService;
        this.aiAnalysisService = aiAnalysisService;
    }

    @Transactional
    public Resume uploadResume(Long userId, MultipartFile file, String jobTitle, String jobDescription) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please provide a valid resume file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Resume file name is required.");
        }

        String lowerName = fileName.toLowerCase();
        if (!(lowerName.endsWith(".pdf") || lowerName.endsWith(".docx") || lowerName.endsWith(".txt"))) {
            throw new IllegalArgumentException("Only PDF, DOCX, and TXT files are supported.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File must be smaller than 10MB.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String extractedText;
        try {
            extractedText = textExtractionService.extractText(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract text from the uploaded file.", e);
        }

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(fileName);
        resume.setFileType(fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase());
        resume.setExtractedText(extractedText);
        Resume savedResume = resumeRepository.save(resume);

        if (jobTitle != null && !jobTitle.isBlank() && jobDescription != null && !jobDescription.isBlank()) {
            JobDescription job = new JobDescription();
            job.setUser(user);
            job.setJobTitle(jobTitle);
            job.setDescription(jobDescription);
            jobDescriptionRepository.save(job);
        }

        return savedResume;
    }

    @Transactional
    public AnalysisResultDto analyzeResume(Long resumeId, String jobDescription) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found."));

        AnalysisResultDto result = aiAnalysisService.analyze(resume.getExtractedText(), jobDescription);

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(resume);
        analysis.setScore(result.getScore());
        analysis.setSkills(toJson(result.getSkills()));
        analysis.setStrengths(toJson(result.getStrengths()));
        analysis.setWeaknesses(toJson(result.getWeaknesses()));
        analysis.setSuggestions(toJson(result.getSuggestions()));
        analysis.setKeywords(toJson(result.getKeywords()));
        analysis.setMissingSkills(toJson(result.getMissingSkills()));
        analysis.setSectionAnalysis(toJson(result.getSectionAnalysis()));
        resumeAnalysisRepository.save(analysis);

        return result;
    }

    public List<ResumeResponse> getResumesForUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return resumeRepository.findByUser(user).stream()
            .map(this::toResumeResponse)
            .toList();
    }

    public ResumeResponse getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found."));
        return toResumeResponse(resume);
    }

    public AnalysisResultDto getLatestAnalysis(Long resumeId) {
        Resume resume = getResumeEntity(resumeId);
        ResumeAnalysis saved = resumeAnalysisRepository.findTopByResumeOrderByCreatedAtDesc(resume)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis not found for this resume."));
        AnalysisResultDto result = new AnalysisResultDto();
        result.setScore(saved.getScore());
        result.setSkills(fromJson(saved.getSkills(), new TypeReference<>() {}));
        result.setMissingSkills(fromJson(saved.getMissingSkills(), new TypeReference<>() {}));
        result.setStrengths(fromJson(saved.getStrengths(), new TypeReference<>() {}));
        result.setWeaknesses(fromJson(saved.getWeaknesses(), new TypeReference<>() {}));
        result.setSuggestions(fromJson(saved.getSuggestions(), new TypeReference<>() {}));
        result.setKeywords(fromJson(saved.getKeywords(), new TypeReference<>() {}));
        result.setSectionAnalysis(fromJson(saved.getSectionAnalysis(), new TypeReference<>() {}));
        return result;
    }

    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = getResumeEntity(resumeId);
        jobMatchAnalysisRepository.deleteByResume(resume);
        resumeAnalysisRepository.deleteByResume(resume);
        resumeRepository.delete(resume);
    }

    public JobMatchResponse analyzeJobMatch(Long resumeId, String jobTitle, String jobDescription) {
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new IllegalArgumentException("Job description is required.");
        }
        Resume resume = getResumeEntity(resumeId);
        AnalysisResultDto result = aiAnalysisService.analyze(resume.getExtractedText(), jobDescription);

        String normalizedJobDescription = jobDescription.toLowerCase(java.util.Locale.ROOT);
        List<String> matchingSkills = result.getSkills().stream()
            .filter(skill -> normalizedJobDescription.contains(skill.toLowerCase(java.util.Locale.ROOT)))
            .toList();
        int totalRequiredSkills = matchingSkills.size() + result.getMissingSkills().size();
        int matchScore = totalRequiredSkills == 0
            ? 0
            : (int) Math.round((double) matchingSkills.size() * 100 / totalRequiredSkills);

        JobDescription job = new JobDescription();
        job.setUser(resume.getUser());
        job.setJobTitle(jobTitle == null || jobTitle.isBlank() ? "Untitled role" : jobTitle.trim());
        job.setDescription(jobDescription);
        job = jobDescriptionRepository.save(job);

        JobMatchAnalysis match = new JobMatchAnalysis();
        match.setResume(resume);
        match.setJobDescription(job);
        match.setMatchScore(matchScore);
        match.setMatchingSkills(toJson(matchingSkills));
        match.setMissingSkills(toJson(result.getMissingSkills()));
        match.setKeywords(toJson(result.getKeywords()));
        match.setRecommendations(toJson(result.getSuggestions()));
        jobMatchAnalysisRepository.save(match);

        return new JobMatchResponse(
            matchScore,
            matchingSkills,
            result.getMissingSkills(),
            result.getKeywords(),
            result.getSuggestions()
        );
    }

    private Resume getResumeEntity(Long resumeId) {
        return resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found."));
    }

    private ResumeResponse toResumeResponse(Resume resume) {
        Optional<ResumeAnalysis> analysis = resumeAnalysisRepository.findTopByResumeOrderByCreatedAtDesc(resume);
        Optional<JobMatchAnalysis> match = jobMatchAnalysisRepository.findTopByResumeOrderByCreatedAtDesc(resume);
        return new ResumeResponse(
            resume.getId(),
            resume.getFileName(),
            resume.getFileType(),
            resume.getUploadedAt(),
            analysis.map(ResumeAnalysis::getScore).orElse(null),
            match.map(JobMatchAnalysis::getMatchScore).orElse(null)
        );
    }

    private <T> List<T> fromJson(String json, TypeReference<List<T>> type) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored analysis data could not be read.", e);
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize resume analysis.", e);
        }
    }
}
