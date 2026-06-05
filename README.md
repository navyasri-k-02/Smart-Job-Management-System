# 🚀 Smart Job Portal System

A production-ready full-stack Job Portal with Email Notifications built with Spring Boot, Thymeleaf, MySQL.

---

## 📁 Project Structure

```
smart-job-portal/
├── pom.xml
├── schema.sql
├── src/main/
│   ├── java/com/jobportal/
│   │   ├── SmartJobPortalApplication.java
│   │   ├── config/
│   │   │   ├── AsyncConfig.java
│   │   │   ├── DataInitializer.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebMvcConfig.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── EmployerController.java
│   │   │   ├── JobController.java
│   │   │   └── StudentController.java
│   │   ├── dto/
│   │   │   ├── JobDto.java
│   │   │   └── RegisterDto.java
│   │   ├── entity/
│   │   │   ├── Application.java
│   │   │   ├── Job.java
│   │   │   ├── OtpToken.java
│   │   │   └── User.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/
│   │   │   ├── ApplicationRepository.java
│   │   │   ├── JobRepository.java
│   │   │   ├── OtpTokenRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       ├── ApplicationService.java
│   │       ├── EmailService.java
│   │       ├── FileStorageService.java
│   │       ├── JobService.java
│   │       └── UserService.java
│   └── resources/
│       ├── application.properties
│       ├── static/css/style.css
│       └── templates/
│           ├── admin/   (dashboard, users, jobs)
│           ├── auth/    (login, register, verify-otp, forgot-password, reset-password)
│           ├── employer/(dashboard, my-jobs, post-job, applicants, profile)
│           ├── error/   (error, access-denied)
│           ├── fragments/(navbar, footer)
│           ├── public/  (jobs, job-detail)
│           └── student/ (dashboard, jobs, job-detail, applications, profile)
```

---

## ✅ Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Gmail Account | With 2FA enabled |

---

## ⚙️ Setup Instructions

### Step 1: MySQL Setup

```sql
-- Create database
CREATE DATABASE smart_job_portal;
-- (Or run schema.sql directly)
mysql -u root -p < schema.sql
```

### Step 2: Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/smart_job_portal?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Gmail SMTP
spring.mail.username=YOUR_GMAIL@gmail.com
spring.mail.password=YOUR_16_CHAR_APP_PASSWORD
```

### Step 3: Gmail App Password (Required for Email)

1. Go to [myaccount.google.com](https://myaccount.google.com)
2. Click **Security**
3. Enable **2-Step Verification** (must be ON)
4. Go back to Security → scroll to **App passwords**
5. Select app: **Mail** | Device: **Other** (type "JobPortal")
6. Click **Generate** → copy the 16-character password
7. Paste it as `spring.mail.password` in application.properties

> ⚠️ Use the 16-char App Password, NOT your regular Gmail password

### Step 4: Build & Run

```bash
cd smart-job-portal
mvn clean install
mvn spring-boot:run
```

### Step 5: Access the App

Open browser → [http://localhost:8080](http://localhost:8080)

---

## 👤 Default Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@smartjobportal.com | admin123 |

> Admin account is auto-created on first startup via `DataInitializer`.

---

## 🔑 Features Summary

### 🎓 Student
- Register with email OTP verification
- Upload resume (PDF only, max 2MB)
- Browse & search jobs (by keyword, location, type, industry)
- Apply with optional cover letter
- Track application status (Pending → Shortlisted/Rejected/Hired)
- Email notifications on every status change

### 🏢 Employer
- Register with company details
- Post/edit/delete job listings
- View all applicants per job
- Shortlist, reject, or hire candidates
- Add personalized notes (emailed to candidate)

### ⚙️ Admin
- View platform-wide analytics
- Activate/deactivate user accounts
- Delete users or job postings

### 📧 Email System
- OTP verification email (with countdown timer on UI)
- Welcome email on verified registration
- Job application confirmation email
- Status update email (with employer note)
- All emails are HTML-formatted, professional design
- Async sending (won't block app if email fails)

---

## 🔒 Security

- BCrypt password hashing
- Spring Security role-based access
- Account locked until email verified
- OTPs expire in 10 minutes
- Max 1 active OTP per email per purpose

---

## 📤 File Upload Rules

- **Only PDF** allowed (validated by MIME type + extension)
- **Max size: 2MB**
- Stored in `/uploads/resumes/` with UUID filename
- Path saved in database

---

## 🌐 URL Routes

| URL | Access |
|-----|--------|
| /jobs | Public |
| /jobs/{id} | Public |
| /login, /register | Public |
| /verify-otp, /forgot-password, /reset-password | Public |
| /student/** | STUDENT role |
| /employer/** | EMPLOYER role |
| /admin/** | ADMIN role |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Email | Spring Mail + Gmail SMTP |
| Frontend | Thymeleaf + Bootstrap 5.3 |
| Database | MySQL 8 + Hibernate |
| Build | Maven |
| Java | 17 |
