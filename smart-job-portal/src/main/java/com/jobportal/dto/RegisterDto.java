package com.jobportal.dto;

import jakarta.validation.constraints.*;

import java.util.Objects;

public class RegisterDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // STUDENT or EMPLOYER

    private String companyName; // required for EMPLOYER

    // =============================================
    // CONSTRUCTORS
    // =============================================

    public RegisterDto() {
    }

    public RegisterDto(String fullName, String email, String password,
                       String role, String companyName) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.companyName = companyName;
    }

    // =============================================
    // GETTERS
    // =============================================

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getCompanyName() { return companyName; }

    // =============================================
    // SETTERS
    // =============================================

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    // =============================================
    // equals, hashCode, toString
    // =============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterDto that = (RegisterDto) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, role);
    }

    @Override
    public String toString() {
        return "RegisterDto{" +
               "fullName='" + fullName + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}
