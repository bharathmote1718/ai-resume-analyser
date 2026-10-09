package com.airesumeanalyzer.dto;

public class SectionAnalysisDto {
    private String section;
    private String status;
    private String details;

    public SectionAnalysisDto() {
    }

    public SectionAnalysisDto(String section, String status, String details) {
        this.section = section;
        this.status = status;
        this.details = details;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
