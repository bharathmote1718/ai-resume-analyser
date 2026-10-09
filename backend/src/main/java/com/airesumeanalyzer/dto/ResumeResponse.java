package com.airesumeanalyzer.dto;

import java.time.LocalDateTime;

public class ResumeResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private LocalDateTime uploadedAt;
    private Integer score;
    private Integer jobMatchScore;

    public ResumeResponse() {
    }

    public ResumeResponse(Long id, String fileName, String fileType, LocalDateTime uploadedAt, Integer score, Integer jobMatchScore) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedAt = uploadedAt;
        this.score = score;
        this.jobMatchScore = jobMatchScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getJobMatchScore() { return jobMatchScore; }
    public void setJobMatchScore(Integer jobMatchScore) { this.jobMatchScore = jobMatchScore; }
}
