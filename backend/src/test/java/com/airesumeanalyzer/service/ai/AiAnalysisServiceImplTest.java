package com.airesumeanalyzer.service.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.airesumeanalyzer.dto.AnalysisResultDto;
import org.junit.jupiter.api.Test;

class AiAnalysisServiceImplTest {
    private final AiAnalysisServiceImpl service = new AiAnalysisServiceImpl();

    @Test
    void doesNotCountJobOnlySkillsAsResumeSkills() {
        AnalysisResultDto result = service.analyze(
            "Java developer. Skills: Java, SQL, Git.",
            "Required skills: Java, SQL, AWS, Docker."
        );

        assertEquals(3, result.getSkills().size());
        assertFalse(result.getSkills().contains("aws"));
        assertFalse(result.getSkills().contains("docker"));
        assertEquals(2, result.getMissingSkills().size());
    }
}
