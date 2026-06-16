package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final JobService jobService;
    private final EmailService emailService;

    public ApplicationService(ApplicationRepository applicationRepository,
                               UserService userService,
                               JobService jobService,
                               EmailService emailService) {
        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.emailService = emailService;
    }

    // =============================================
    // STUDENT APPLIES FOR JOB
    // =============================================
    @Transactional
    public Application applyForJob(Long jobId, String studentEmail, String coverLetter) {
        User student = userService.getUserByEmail(studentEmail);
        Job job = jobService.getJobById(jobId);

        if (applicationRepository.existsByStudentAndJob(student, job)) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        if (student.getResumePath() == null || student.getResumePath().isBlank()) {
            throw new IllegalStateException("Please upload your resume before applying.");
        }

        Application application = Application.builder()
                .student(student)
                .job(job)
                .coverLetter(coverLetter)
                .resumePath(student.getResumePath())
                .status(Application.ApplicationStatus.PENDING)
                .build();

        application = applicationRepository.save(application);

        // Send confirmation email
        emailService.sendApplicationConfirmationEmail(
                student.getEmail(),
                student.getFullName(),
                job.getTitle(),
                job.getCompanyName()
        );

        return application;
    }

    // =============================================
    // STUDENT: VIEW OWN APPLICATIONS
    // =============================================
    public List<Application> getStudentApplications(String studentEmail) {
        User student = userService.getUserByEmail(studentEmail);
        return applicationRepository.findByStudent(student);
    }

    // =============================================
    // EMPLOYER: VIEW APPLICANTS FOR A JOB
    // =============================================
    public List<Application> getJobApplications(Long jobId, String employerEmail) {
        Job job = jobService.getJobById(jobId);
        // Verify ownership
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new SecurityException("Access denied.");
        }
        return applicationRepository.findByJob(job);
    }

    public List<Application> getAllEmployerApplications(String employerEmail) {
        List<Job> jobs = jobService.getEmployerJobs(employerEmail);
        return applicationRepository.findByJobIn(jobs);
    }

    // =============================================
    // EMPLOYER: UPDATE APPLICATION STATUS
    // =============================================
    @Transactional
    public Application updateApplicationStatus(Long applicationId, String status,
                                                String note, String employerEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify employer owns the job
        if (!application.getJob().getEmployer().getEmail().equals(employerEmail)) {
            throw new SecurityException("Access denied.");
        }

        Application.ApplicationStatus newStatus = Application.ApplicationStatus.valueOf(status);
        application.setStatus(newStatus);
        application.setEmployerNote(note);
        application = applicationRepository.save(application);

        // Send status update email to student
        emailService.sendApplicationStatusUpdateEmail(
                application.getStudent().getEmail(),
                application.getStudent().getFullName(),
                application.getJob().getTitle(),
                application.getJob().getCompanyName(),
                status,
                note
        );

        return application;
    }

    // =============================================
    // CHECK IF STUDENT ALREADY APPLIED
    // =============================================
    public boolean hasApplied(Long jobId, String studentEmail) {
        try {
            User student = userService.getUserByEmail(studentEmail);
            Job job = jobService.getJobById(jobId);
            return applicationRepository.existsByStudentAndJob(student, job);
        } catch (Exception e) {
            return false;
        }
    }

    // =============================================
    // ADMIN / STATS
    // =============================================
    public long countTotalApplications() {
        return applicationRepository.count();
    }

    public long countPendingApplications() {
        return applicationRepository.countPending();
    }

    public long countEmployerApplications(Long employerId) {
        return applicationRepository.countByEmployerId(employerId);
    }
}
