package com.airesumeanalyzer.controller;

import com.airesumeanalyzer.dto.AnalysisResultDto;
import com.airesumeanalyzer.dto.ResumeResponse;
import com.airesumeanalyzer.service.ResumeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes/upload")
    public ResponseEntity<ResumeResponse> uploadResume(
        @RequestParam Long userId,
        @RequestParam(required = false) String jobTitle,
        @RequestParam(required = false) String jobDescription,
        @RequestParam("file") MultipartFile file
    ) {
        var uploaded = resumeService.uploadResume(userId, file, jobTitle, jobDescription);
        return ResponseEntity.ok(resumeService.getResume(uploaded.getId()));
    }

    @PostMapping("/analysis/{resumeId}")
    public ResponseEntity<AnalysisResultDto> analyzeResume(
        @PathVariable Long resumeId,
        @RequestParam(required = false) String jobDescription
    ) {
        return ResponseEntity.ok(resumeService.analyzeResume(resumeId, jobDescription));
    }

    @GetMapping("/resumes")
    public ResponseEntity<List<ResumeResponse>> getResumes(@RequestParam Long userId) {
        return ResponseEntity.ok(resumeService.getResumesForUser(userId));
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResumeResponse> getResume(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResume(id));
    }

    @GetMapping("/analysis/{resumeId}")
    public ResponseEntity<AnalysisResultDto> getAnalysis(@PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getLatestAnalysis(resumeId));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
}
