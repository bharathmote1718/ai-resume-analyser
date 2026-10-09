package com.airesumeanalyzer.dto;

import java.util.List;

public class JobMatchResponse {
    private int matchScore;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private List<String> keywords;
    private List<String> recommendations;

    public JobMatchResponse() {
    }

    public JobMatchResponse(int matchScore, List<String> matchingSkills, List<String> missingSkills, List<String> keywords, List<String> recommendations) {
        this.matchScore = matchScore;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
        this.keywords = keywords;
        this.recommendations = recommendations;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
