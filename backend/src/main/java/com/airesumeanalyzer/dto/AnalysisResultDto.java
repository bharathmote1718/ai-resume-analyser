package com.airesumeanalyzer.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResultDto {
    private int score;
    private List<String> skills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private List<SectionAnalysisDto> sectionAnalysis = new ArrayList<>();

    public AnalysisResultDto() {
    }

    public AnalysisResultDto(int score, List<String> skills, List<String> missingSkills, List<String> strengths, List<String> weaknesses, List<String> suggestions, List<String> keywords, List<SectionAnalysisDto> sectionAnalysis) {
        this.score = score;
        this.skills = skills;
        this.missingSkills = missingSkills;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
        this.keywords = keywords;
        this.sectionAnalysis = sectionAnalysis;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<SectionAnalysisDto> getSectionAnalysis() {
        return sectionAnalysis;
    }

    public void setSectionAnalysis(List<SectionAnalysisDto> sectionAnalysis) {
        this.sectionAnalysis = sectionAnalysis;
    }
}
