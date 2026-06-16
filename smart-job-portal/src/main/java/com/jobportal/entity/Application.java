package com.jobportal.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "job_id"}))
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String resumePath; // snapshot of resume at time of application

    private String employerNote; // note from employer

    @CreationTimestamp
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public Application() {
    }

    private Application(Builder builder) {
        this.id = builder.id;
        this.student = builder.student;
        this.job = builder.job;
        this.status = builder.status;
        this.coverLetter = builder.coverLetter;
        this.resumePath = builder.resumePath;
        this.employerNote = builder.employerNote;
        this.appliedAt = builder.appliedAt;
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
        private User student;
        private Job job;
        private ApplicationStatus status = ApplicationStatus.PENDING;
        private String coverLetter;
        private String resumePath;
        private String employerNote;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(Long id) { this.id = id; return this; }
        public Builder student(User student) { this.student = student; return this; }
        public Builder job(Job job) { this.job = job; return this; }
        public Builder status(ApplicationStatus status) { this.status = status; return this; }
        public Builder coverLetter(String coverLetter) { this.coverLetter = coverLetter; return this; }
        public Builder resumePath(String resumePath) { this.resumePath = resumePath; return this; }
        public Builder employerNote(String employerNote) { this.employerNote = employerNote; return this; }
        public Builder appliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Application build() {
            return new Application(this);
        }
    }

    // =============================================
    // GETTERS
    // =============================================

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Job getJob() { return job; }
    public ApplicationStatus getStatus() { return status; }
    public String getCoverLetter() { return coverLetter; }
    public String getResumePath() { return resumePath; }
    public String getEmployerNote() { return employerNote; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // =============================================
    // SETTERS
    // =============================================

    public void setId(Long id) { this.id = id; }
    public void setStudent(User student) { this.student = student; }
    public void setJob(Job job) { this.job = job; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public void setEmployerNote(String employerNote) { this.employerNote = employerNote; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // =============================================
    // equals & hashCode (exclude lazy relations)
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =============================================
    // toString (exclude lazy relations)
    // =============================================

    @Override
    public String toString() {
        return "Application{" +
               "id=" + id +
               ", status=" + status +
               ", appliedAt=" + appliedAt +
               '}';
    }

    // =============================================
    // ENUM
    // =============================================

    public enum ApplicationStatus {
        PENDING, REVIEWED, SHORTLISTED, REJECTED, HIRED
    }
}
