package com.airesumeanalyzer.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
            "application", "AI Resume Analyzer API",
            "status", "UP",
            "frontend", "http://localhost:5173",
            "health", "http://localhost:8080/api/health"
        );
    }
}
