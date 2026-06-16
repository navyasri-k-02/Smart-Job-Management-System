package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Objects;

public class JobDto {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String companyName;
    private String location;
    private String jobType;
    private String salary;
    private String experience;
    private String skills;
    private String education;
    private String industry;
    private LocalDate deadline;

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public JobDto() {
    }

    public JobDto(String title, String description, String companyName, String location,
                  String jobType, String salary, String experience, String skills,
                  String education, String industry, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.salary = salary;
        this.experience = experience;
        this.skills = skills;
        this.education = education;
        this.industry = industry;
        this.deadline = deadline;
    }

    // =============================================
    // GETTERS
    // =============================================

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCompanyName() { return companyName; }
    public String getLocation() { return location; }
    public String getJobType() { return jobType; }
    public String getSalary() { return salary; }
    public String getExperience() { return experience; }
    public String getSkills() { return skills; }
    public String getEducation() { return education; }
    public String getIndustry() { return industry; }
    public LocalDate getDeadline() { return deadline; }

    // =============================================
    // SETTERS
    // =============================================

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setLocation(String location) { this.location = location; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setEducation(String education) { this.education = education; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    // =============================================
    // equals, hashCode, toString
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDto jobDto = (JobDto) o;
        return Objects.equals(title, jobDto.title) &&
               Objects.equals(description, jobDto.description) &&
               Objects.equals(companyName, jobDto.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, companyName);
    }

    @Override
    public String toString() {
        return "JobDto{" +
               "title='" + title + '\'' +
               ", companyName='" + companyName + '\'' +
               ", location='" + location + '\'' +
               ", jobType='" + jobType + '\'' +
               '}';
    }
}
