package com.jobportal.service;

import com.jobportal.dto.JobDto;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserService userService;

    public JobService(JobRepository jobRepository, UserService userService) {
        this.jobRepository = jobRepository;
        this.userService = userService;
    }

    // =============================================
    // PUBLIC JOB BROWSING
    // =============================================
    public List<Job> getActiveJobs() {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE);
    }

    public List<Job> searchJobs(String keyword, String location, String jobType, String industry) {
        String k = (keyword != null && keyword.isBlank()) ? null : keyword;
        String l = (location != null && location.isBlank()) ? null : location;
        String jt = (jobType != null && jobType.isBlank()) ? null : jobType;
        String ind = (industry != null && industry.isBlank()) ? null : industry;
        return jobRepository.searchJobs(k, l, jt, ind);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
    }

    public List<Job> getLatestJobs() {
        return jobRepository.findTop6ByStatusOrderByCreatedAtDesc(Job.JobStatus.ACTIVE);
    }

    // =============================================
    // EMPLOYER JOB MANAGEMENT
    // =============================================
    @Transactional
    public Job createJob(JobDto dto, String employerEmail) {
        User employer = userService.getUserByEmail(employerEmail);

        Job job = Job.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .companyName(employer.getCompanyName() != null ? employer.getCompanyName() : dto.getCompanyName())
                .location(dto.getLocation())
                .jobType(dto.getJobType())
                .salary(dto.getSalary())
                .experience(dto.getExperience())
                .skills(dto.getSkills())
                .education(dto.getEducation())
                .industry(dto.getIndustry())
                .deadline(dto.getDeadline())
                .status(Job.JobStatus.ACTIVE)
                .employer(employer)
                .build();

        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long jobId, JobDto dto, String employerEmail) {
        Job job = getJobById(jobId);
        validateOwnership(job, employerEmail);

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setSalary(dto.getSalary());
        job.setExperience(dto.getExperience());
        job.setSkills(dto.getSkills());
        job.setEducation(dto.getEducation());
        job.setIndustry(dto.getIndustry());
        job.setDeadline(dto.getDeadline());

        return jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId, String employerEmail) {
        Job job = getJobById(jobId);
        validateOwnership(job, employerEmail);
        jobRepository.delete(job);
    }

    @Transactional
    public void toggleJobStatus(Long jobId, String employerEmail) {
        Job job = getJobById(jobId);
        validateOwnership(job, employerEmail);
        job.setStatus(job.getStatus() == Job.JobStatus.ACTIVE ? Job.JobStatus.CLOSED : Job.JobStatus.ACTIVE);
        jobRepository.save(job);
    }

    public List<Job> getEmployerJobs(String employerEmail) {
        User employer = userService.getUserByEmail(employerEmail);
        return jobRepository.findByEmployer(employer);
    }

    // =============================================
    // ADMIN
    // =============================================
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Transactional
    public void adminDeleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public long countActiveJobs() {
        return jobRepository.countByStatus(Job.JobStatus.ACTIVE);
    }

    public long countTotalJobs() {
        return jobRepository.count();
    }

    // =============================================
    // HELPER
    // =============================================
    private void validateOwnership(Job job, String employerEmail) {
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new SecurityException("Access denied: You don't own this job.");
        }
    }
}
