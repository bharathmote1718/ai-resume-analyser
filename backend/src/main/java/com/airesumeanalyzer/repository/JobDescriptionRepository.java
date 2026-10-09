package com.airesumeanalyzer.repository;

import com.airesumeanalyzer.entity.JobDescription;
import com.airesumeanalyzer.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    List<JobDescription> findByUser(User user);
}
