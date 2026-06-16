-- =============================================
-- Smart Job Portal - Database Schema
-- =============================================

CREATE DATABASE IF NOT EXISTS smart_job_portal;
USE smart_job_portal;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'EMPLOYER', 'ADMIN') NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    phone VARCHAR(20),
    location VARCHAR(255),
    bio TEXT,
    resume_path VARCHAR(500),
    skills TEXT,
    experience VARCHAR(100),
    education VARCHAR(255),
    company_name VARCHAR(255),
    company_website VARCHAR(500),
    company_description TEXT,
    company_size VARCHAR(50),
    industry VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Jobs Table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    job_type VARCHAR(50),
    salary VARCHAR(100),
    experience VARCHAR(100),
    skills TEXT,
    education VARCHAR(255),
    industry VARCHAR(100),
    deadline DATE,
    status ENUM('ACTIVE', 'CLOSED', 'DRAFT') DEFAULT 'ACTIVE',
    employer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    status ENUM('PENDING', 'REVIEWED', 'SHORTLISTED', 'REJECTED', 'HIRED') DEFAULT 'PENDING',
    cover_letter TEXT,
    resume_path VARCHAR(500),
    employer_note TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_application (student_id, job_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- OTP Tokens Table
CREATE TABLE IF NOT EXISTS otp_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    purpose ENUM('EMAIL_VERIFICATION', 'PASSWORD_RESET') NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    INDEX idx_otp_email_purpose (email, purpose)
);

-- =============================================
-- Sample Admin Account
-- Password: admin123 (BCrypt encoded)
-- =============================================
INSERT IGNORE INTO users (full_name, email, password, role, enabled, created_at)
VALUES (
    'Admin',
    'admin@smartjobportal.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpyycXGUZa',
    'ADMIN',
    TRUE,
    NOW()
);

-- =============================================
-- Sample Employer Account
-- Password: employer123
-- =============================================
INSERT IGNORE INTO users (full_name, email, password, role, enabled, company_name, industry, created_at)
VALUES (
    'Tech Corp HR',
    'employer@techcorp.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpyycXGUZa',
    'EMPLOYER',
    TRUE,
    'Tech Corp',
    'Technology',
    NOW()
);
