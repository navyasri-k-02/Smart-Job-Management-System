package com.jobportal.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String companyName;

    private String location;
    private String jobType; // Full-time, Part-time, Internship, Remote
    private String salary;
    private String experience;
    private String skills;
    private String education;
    private String industry;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public Job() {
    }

    private Job(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.companyName = builder.companyName;
        this.location = builder.location;
        this.jobType = builder.jobType;
        this.salary = builder.salary;
        this.experience = builder.experience;
        this.skills = builder.skills;
        this.education = builder.education;
        this.industry = builder.industry;
        this.deadline = builder.deadline;
        this.status = builder.status;
        this.employer = builder.employer;
        this.applications = builder.applications;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // =============================================
    // BUILDER
    // =============================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
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
        private JobStatus status = JobStatus.ACTIVE;
        private User employer;
        private List<Application> applications;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder jobType(String jobType) { this.jobType = jobType; return this; }
        public Builder salary(String salary) { this.salary = salary; return this; }
        public Builder experience(String experience) { this.experience = experience; return this; }
        public Builder skills(String skills) { this.skills = skills; return this; }
        public Builder education(String education) { this.education = education; return this; }
        public Builder industry(String industry) { this.industry = industry; return this; }
        public Builder deadline(LocalDate deadline) { this.deadline = deadline; return this; }
        public Builder status(JobStatus status) { this.status = status; return this; }
        public Builder employer(User employer) { this.employer = employer; return this; }
        public Builder applications(List<Application> applications) { this.applications = applications; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Job build() {
            return new Job(this);
        }
    }

    // =============================================
    // GETTERS
    // =============================================

    public Long getId() { return id; }
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
    public JobStatus getStatus() { return status; }
    public User getEmployer() { return employer; }
    public List<Application> getApplications() { return applications; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // =============================================
    // SETTERS
    // =============================================

    public void setId(Long id) { this.id = id; }
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
    public void setStatus(JobStatus status) { this.status = status; }
    public void setEmployer(User employer) { this.employer = employer; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // =============================================
    // equals & hashCode (exclude lazy relations/collections)
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =============================================
    // toString (exclude lazy relations/collections)
    // =============================================

    @Override
    public String toString() {
        return "Job{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", companyName='" + companyName + '\'' +
               ", status=" + status +
               ", createdAt=" + createdAt +
               '}';
    }

    // =============================================
    // ENUM
    // =============================================

    public enum JobStatus {
        ACTIVE, CLOSED, DRAFT
    }
}
