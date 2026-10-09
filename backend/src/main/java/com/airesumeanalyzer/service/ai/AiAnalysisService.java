package com.airesumeanalyzer.service.ai;

import com.airesumeanalyzer.dto.AnalysisResultDto;

public interface AiAnalysisService {
    AnalysisResultDto analyze(String resumeText, String jobDescription);
}
