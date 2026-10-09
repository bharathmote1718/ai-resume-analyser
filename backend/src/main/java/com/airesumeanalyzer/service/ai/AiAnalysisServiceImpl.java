package com.airesumeanalyzer.service.ai;

import com.airesumeanalyzer.dto.AnalysisResultDto;
import com.airesumeanalyzer.dto.SectionAnalysisDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Value("${ai.api.key:}")
    private String apiKey;

    private static final List<String> DEFAULT_SKILLS = Arrays.asList(
        "java", "javascript", "python", "html", "css", "react", "spring boot", "sql",
        "mysql", "git", "rest api", "problem solving", "communication", "leadership",
        "machine learning", "data structures", "aws", "docker", "microservices", "c++",
        "c#", "nodejs", "express", "typescript", "agile", "debugging"
    );

    @Override
    public AnalysisResultDto analyze(String resumeText, String jobDescription) {
        String normalizedResume = normalize(resumeText);
        String normalizedJob = normalize(jobDescription);

        List<String> detectedSkills = detectSkills(normalizedResume);
        List<String> missingSkills = detectMissingSkills(normalizedJob, detectedSkills);
        List<String> keywords = extractKeywords(normalizedResume, normalizedJob);
        int score = calculateScore(normalizedResume, normalizedJob, detectedSkills, missingSkills);

        List<String> strengths = new ArrayList<>();
        if (containsAny(normalizedResume, "java", "spring", "sql", "react", "python")) {
            strengths.add("Strong technical foundation with relevant technology exposure.");
        }
        if (containsAny(normalizedResume, "education", "bachelor", "master", "degree")) {
            strengths.add("Education background is clearly documented.");
        }
        if (containsAny(normalizedResume, "project", "experience", "internship", "volunteer")) {
            strengths.add("Practical project or work experience is highlighted.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Resume shows a clear professional profile with room for stronger detail.");
        }

        List<String> weaknesses = new ArrayList<>();
        if (!containsAny(normalizedResume, "summary", "objective", "profile")) {
            weaknesses.add("Add a compelling professional summary to strengthen the opening section.");
        }
        if (!containsAny(normalizedResume, "project", "experience", "internship")) {
            weaknesses.add("Include more project or experience details with measurable outcomes.");
        }
        if (missingSkills.size() > 2) {
            weaknesses.add("The resume could better align with job-specific technologies and keywords.");
        }

        List<String> suggestions = new ArrayList<>();
        suggestions.add("Add measurable achievements to your experience and project descriptions.");
        if (missingSkills != null && !missingSkills.isEmpty()) {
            suggestions.add("Include specific technologies such as " + String.join(", ", missingSkills.stream().limit(3).toList()) + " when relevant.");
        } else {
            suggestions.add("Add a few high-impact technologies and tools that match the job market to strengthen keyword relevance.");
        }
        suggestions.add("Improve the summary section with measurable outcomes and impact.");
        suggestions.add("Use action-oriented language such as developed, led, optimized, and delivered.");

        List<SectionAnalysisDto> sectionAnalysis = new ArrayList<>();
        sectionAnalysis.add(new SectionAnalysisDto("Summary", hasSection(normalizedResume, "summary", "objective", "profile") ? "Good" : "Needs improvement", "Professional overview is " + (hasSection(normalizedResume, "summary", "objective", "profile") ? "clearly present." : "missing or weak.")));
        sectionAnalysis.add(new SectionAnalysisDto("Education", hasSection(normalizedResume, "education", "degree", "bachelor", "master") ? "Good" : "Needs improvement", "Academic background is " + (hasSection(normalizedResume, "education", "degree", "bachelor", "master") ? "present." : "not clearly highlighted.")));
        sectionAnalysis.add(new SectionAnalysisDto("Skills", detectedSkills.isEmpty() ? "Needs improvement" : "Good", "Detected technologies: " + (detectedSkills.isEmpty() ? "none" : detectedSkills.stream().limit(6).collect(Collectors.joining(", ")))));
        sectionAnalysis.add(new SectionAnalysisDto("Projects", hasSection(normalizedResume, "project", "portfolio") ? "Good" : "Needs improvement", "Projects are " + (hasSection(normalizedResume, "project", "portfolio") ? "visible in the document." : "not prominent enough.")));
        sectionAnalysis.add(new SectionAnalysisDto("Experience", hasSection(normalizedResume, "experience", "internship", "worked") ? "Good" : "Needs improvement", "Work experience section can be strengthened with metrics."));
        sectionAnalysis.add(new SectionAnalysisDto("Certifications", hasSection(normalizedResume, "certification", "aws", "oracle", "azure") ? "Good" : "Needs improvement", "Credentials are " + (hasSection(normalizedResume, "certification", "aws", "oracle", "azure") ? "listed." : "not clearly visible.")));

        AnalysisResultDto result = new AnalysisResultDto();
        result.setScore(score);
        result.setSkills(detectedSkills);
        result.setMissingSkills(missingSkills);
        result.setStrengths(strengths);
        result.setWeaknesses(weaknesses);
        result.setSuggestions(suggestions);
        result.setKeywords(keywords);
        result.setSectionAnalysis(sectionAnalysis);
        return result;
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private List<String> detectSkills(String resumeText) {
        Set<String> found = new HashSet<>();

        for (String skill : DEFAULT_SKILLS) {
            if (resumeText.contains(skill)) {
                found.add(skill);
            }
        }

        if (resumeText.contains("java") && !found.contains("java")) {
            found.add("java");
        }
        if (resumeText.contains("spring") && !found.contains("spring boot")) {
            found.add("spring boot");
        }

        List<String> result = new ArrayList<>(found);
        result.sort(String::compareTo);
        return result;
    }

    private List<String> detectMissingSkills(String jobText, List<String> detectedSkills) {
        if (jobText == null || jobText.isBlank()) {
            return new ArrayList<>();
        }
        List<String> missing = new ArrayList<>();
        for (String skill : DEFAULT_SKILLS) {
            if (jobText.contains(skill) && !detectedSkills.contains(skill)) {
                missing.add(skill);
            }
        }
        return missing.stream().limit(6).collect(Collectors.toList());
    }

    private List<String> extractKeywords(String resumeText, String jobText) {
        Set<String> keywords = new HashSet<>();
        for (String phrase : Arrays.asList("java", "spring boot", "sql", "mysql", "react", "rest api", "problem solving", "communication", "leadership", "git", "aws", "docker")) {
            if ((resumeText + " " + jobText).contains(phrase)) {
                keywords.add(phrase);
            }
        }
        return new ArrayList<>(keywords);
    }

    private int calculateScore(String resumeText, String jobText, List<String> detectedSkills, List<String> missingSkills) {
        int score = 58;
        if (resumeText.contains("education") || resumeText.contains("degree") || resumeText.contains("bachelor") || resumeText.contains("master")) {
            score += 10;
        }
        if (resumeText.contains("project") || resumeText.contains("experience") || resumeText.contains("internship")) {
            score += 12;
        }
        if (resumeText.contains("summary") || resumeText.contains("objective") || resumeText.contains("profile")) {
            score += 8;
        }
        if (!detectedSkills.isEmpty()) {
            score += Math.min(12, detectedSkills.size() * 2);
        }
        if (jobText != null && !jobText.isBlank()) {
            score += Math.min(10, 5 + Math.max(0, 10 - missingSkills.size()));
        }
        return Math.min(score, 100);
    }

    private boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSection(String text, String... patterns) {
        return containsAny(text, patterns);
    }
}
