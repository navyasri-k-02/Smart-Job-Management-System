package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;

    public StudentController(UserService userService,
                             JobService jobService,
                             ApplicationService applicationService,
                             FileStorageService fileStorageService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.fileStorageService = fileStorageService;
    }

    // =============================================
    // DASHBOARD
    // =============================================
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Application> applications = applicationService.getStudentApplications(user.getEmail());
        List<Job> latestJobs = jobService.getLatestJobs();

        long pending = applications.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.PENDING).count();
        long shortlisted = applications.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.SHORTLISTED).count();
        long hired = applications.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.HIRED).count();

        model.addAttribute("user", user);
        model.addAttribute("totalApplications", applications.size());
        model.addAttribute("pendingApplications", pending);
        model.addAttribute("shortlistedApplications", shortlisted);
        model.addAttribute("hiredApplications", hired);
        model.addAttribute("latestJobs", latestJobs);
        model.addAttribute("recentApplications", applications.stream().limit(5).toList());
        return "student/dashboard";
    }

    // =============================================
    // JOB BROWSING
    // =============================================
    @GetMapping("/jobs")
    public String browseJobs(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String jobType,
                             @RequestParam(required = false) String industry,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        List<Job> jobs;
        if (keyword != null || location != null || jobType != null || industry != null) {
            jobs = jobService.searchJobs(keyword, location, jobType, industry);
        } else {
            jobs = jobService.getActiveJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("jobType", jobType);
        model.addAttribute("industry", industry);
        return "student/jobs";
    }

    // =============================================
    // JOB DETAIL + APPLY
    // =============================================
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        Job job = jobService.getJobById(id);
        boolean alreadyApplied = applicationService.hasApplied(id, userDetails.getUsername());
        User user = userService.getUserByEmail(userDetails.getUsername());

        model.addAttribute("job", job);
        model.addAttribute("alreadyApplied", alreadyApplied);
        model.addAttribute("hasResume", user.getResumePath() != null && !user.getResumePath().isBlank());
        return "student/job-detail";
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable Long id,
                              @RequestParam(required = false) String coverLetter,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            applicationService.applyForJob(id, userDetails.getUsername(), coverLetter);
            redirectAttributes.addFlashAttribute("success",
                    "Application submitted successfully! You'll receive a confirmation email.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/applications";
    }

    // =============================================
    // APPLICATIONS
    // =============================================
    @GetMapping("/applications")
    public String myApplications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Application> applications = applicationService.getStudentApplications(userDetails.getUsername());
        model.addAttribute("applications", applications);
        return "student/applications";
    }

    // =============================================
    // PROFILE
    // =============================================
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute User updatedUser,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(userDetails.getUsername(), updatedUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    // =============================================
    // RESUME UPLOAD
    // =============================================
    @PostMapping("/resume/upload")
    public String uploadResume(@RequestParam("resume") MultipartFile file,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            String path = fileStorageService.storeResume(file);
            userService.updateResumePath(userDetails.getUsername(), path);
            redirectAttributes.addFlashAttribute("success",
                    "Resume uploaded successfully! You can now apply for jobs.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload resume: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }
}
