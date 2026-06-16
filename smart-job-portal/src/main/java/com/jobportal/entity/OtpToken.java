package com.jobportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "otp_tokens")
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public OtpToken() {
    }

    private OtpToken(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.otp = builder.otp;
        this.purpose = builder.purpose;
        this.expiresAt = builder.expiresAt;
        this.used = builder.used;
    }

    // =============================================
    // BUILDER
    // =============================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String email;
        private String otp;
        private OtpPurpose purpose;
        private LocalDateTime expiresAt;
        private boolean used = false;

        private Builder() {
        }

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder otp(String otp) { this.otp = otp; return this; }
        public Builder purpose(OtpPurpose purpose) { this.purpose = purpose; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder used(boolean used) { this.used = used; return this; }

        public OtpToken build() {
            return new OtpToken(this);
        }
    }

    // =============================================
    // GETTERS
    // =============================================

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public OtpPurpose getPurpose() { return purpose; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }

    // =============================================
    // SETTERS
    // =============================================

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setUsed(boolean used) { this.used = used; }

    // =============================================
    // BUSINESS METHOD
    // =============================================

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // =============================================
    // equals & hashCode
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpToken otpToken = (OtpToken) o;
        return Objects.equals(id, otpToken.id) &&
               Objects.equals(email, otpToken.email) &&
               Objects.equals(otp, otpToken.otp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, otp);
    }

    @Override
    public String toString() {
        return "OtpToken{" +
               "id=" + id +
               ", email='" + email + '\'' +
               ", purpose=" + purpose +
               ", expiresAt=" + expiresAt +
               ", used=" + used +
               '}';
    }

    // =============================================
    // ENUM
    // =============================================

    public enum OtpPurpose {
        EMAIL_VERIFICATION, PASSWORD_RESET
    }
}
