package com.airesumeanalyzer.repository;

import com.airesumeanalyzer.entity.Resume;
import com.airesumeanalyzer.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUser(User user);
}
