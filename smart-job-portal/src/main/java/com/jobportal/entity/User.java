package com.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = false;

    private String phone;
    private String location;
    private String bio;

    // Student-specific fields
    private String resumePath;
    private String skills;
    private String experience;
    private String education;

    // Employer-specific fields
    private String companyName;
    private String companyWebsite;
    private String companyDescription;
    private String companySize;
    private String industry;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Application> applications;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<Job> postedJobs;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public User() {
    }

    private User(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.enabled = builder.enabled;
        this.phone = builder.phone;
        this.location = builder.location;
        this.bio = builder.bio;
        this.resumePath = builder.resumePath;
        this.skills = builder.skills;
        this.experience = builder.experience;
        this.education = builder.education;
        this.companyName = builder.companyName;
        this.companyWebsite = builder.companyWebsite;
        this.companyDescription = builder.companyDescription;
        this.companySize = builder.companySize;
        this.industry = builder.industry;
        this.applications = builder.applications;
        this.postedJobs = builder.postedJobs;
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
        private String fullName;
        private String email;
        private String password;
        private Role role;
        private boolean enabled = false;
        private String phone;
        private String location;
        private String bio;
        private String resumePath;
        private String skills;
        private String experience;
        private String education;
        private String companyName;
        private String companyWebsite;
        private String companyDescription;
        private String companySize;
        private String industry;
        private List<Application> applications;
        private List<Job> postedJobs;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(Long id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder resumePath(String resumePath) { this.resumePath = resumePath; return this; }
        public Builder skills(String skills) { this.skills = skills; return this; }
        public Builder experience(String experience) { this.experience = experience; return this; }
        public Builder education(String education) { this.education = education; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder companyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; return this; }
        public Builder companyDescription(String companyDescription) { this.companyDescription = companyDescription; return this; }
        public Builder companySize(String companySize) { this.companySize = companySize; return this; }
        public Builder industry(String industry) { this.industry = industry; return this; }
        public Builder applications(List<Application> applications) { this.applications = applications; return this; }
        public Builder postedJobs(List<Job> postedJobs) { this.postedJobs = postedJobs; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            return new User(this);
        }
    }

    // =============================================
    // GETTERS
    // =============================================

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getBio() { return bio; }
    public String getResumePath() { return resumePath; }
    public String getSkills() { return skills; }
    public String getExperience() { return experience; }
    public String getEducation() { return education; }
    public String getCompanyName() { return companyName; }
    public String getCompanyWebsite() { return companyWebsite; }
    public String getCompanyDescription() { return companyDescription; }
    public String getCompanySize() { return companySize; }
    public String getIndustry() { return industry; }
    public List<Application> getApplications() { return applications; }
    public List<Job> getPostedJobs() { return postedJobs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // =============================================
    // SETTERS
    // =============================================

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setBio(String bio) { this.bio = bio; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setEducation(String education) { this.education = education; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
    public void setPostedJobs(List<Job> postedJobs) { this.postedJobs = postedJobs; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // =============================================
    // equals & hashCode (exclude collections to avoid circular references)
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // =============================================
    // toString (exclude collections to avoid circular references)
    // =============================================

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", fullName='" + fullName + '\'' +
               ", email='" + email + '\'' +
               ", role=" + role +
               ", enabled=" + enabled +
               ", createdAt=" + createdAt +
               '}';
    }

    // =============================================
    // ENUM
    // =============================================

    public enum Role {
        STUDENT, EMPLOYER, ADMIN
    }
}
