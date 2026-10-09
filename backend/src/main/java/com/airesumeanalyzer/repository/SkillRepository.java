package com.airesumeanalyzer.repository;

import com.airesumeanalyzer.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
