package com.airesumeanalyzer.controller;

import com.airesumeanalyzer.dto.JobMatchRequest;
import com.airesumeanalyzer.dto.JobMatchResponse;
import com.airesumeanalyzer.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JobMatchController {
    private final ResumeService resumeService;

    public JobMatchController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/job-match/analyze")
    public ResponseEntity<JobMatchResponse> analyzeJobMatch(@Valid @RequestBody JobMatchRequest request) {
        return ResponseEntity.ok(
            resumeService.analyzeJobMatch(request.getResumeId(), request.getJobTitle(), request.getJobDescription())
        );
    }
}
