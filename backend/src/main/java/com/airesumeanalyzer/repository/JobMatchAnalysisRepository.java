package com.airesumeanalyzer.repository;

import com.airesumeanalyzer.entity.JobMatchAnalysis;
import com.airesumeanalyzer.entity.Resume;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobMatchAnalysisRepository extends JpaRepository<JobMatchAnalysis, Long> {
    Optional<JobMatchAnalysis> findTopByResumeOrderByCreatedAtDesc(Resume resume);
    void deleteByResume(Resume resume);
}
