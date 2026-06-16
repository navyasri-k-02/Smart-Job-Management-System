package com.jobportal.controller;

import com.jobportal.dto.JobDto;
import com.jobportal.entity.*;
import com.jobportal.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public EmployerController(UserService userService,
                              JobService jobService,
                              ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    // =============================================
    // DASHBOARD
    // =============================================
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User employer = userService.getUserByEmail(userDetails.getUsername());
        List<Job> jobs = jobService.getEmployerJobs(userDetails.getUsername());
        long totalApplications = applicationService.countEmployerApplications(employer.getId());
        List<Application> recentApplications = applicationService.getAllEmployerApplications(userDetails.getUsername())
                .stream().limit(5).toList();

        long activeJobs = jobs.stream().filter(j -> j.getStatus() == Job.JobStatus.ACTIVE).count();

        model.addAttribute("user", employer);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("recentApplications", recentApplications);
        return "employer/dashboard";
    }

    // =============================================
    // JOB MANAGEMENT
    // =============================================
    @GetMapping("/jobs")
    public String myJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Job> jobs = jobService.getEmployerJobs(userDetails.getUsername());
        model.addAttribute("jobs", jobs);
        return "employer/my-jobs";
    }

    @GetMapping("/jobs/post")
    public String postJobPage(Model model) {
        model.addAttribute("jobDto", new JobDto());
        return "employer/post-job";
    }

    @PostMapping("/jobs/post")
    public String postJob(@Valid @ModelAttribute JobDto jobDto,
                          BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("jobDto", jobDto);
            return "employer/post-job";
        }
        try {
            jobService.createJob(jobDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to post job: " + e.getMessage());
        }
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobPage(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Job job = jobService.getJobById(id);
        JobDto dto = new JobDto();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setSalary(job.getSalary());
        dto.setExperience(job.getExperience());
        dto.setSkills(job.getSkills());
        dto.setEducation(job.getEducation());
        dto.setIndustry(job.getIndustry());
        dto.setDeadline(job.getDeadline());
        model.addAttribute("jobDto", dto);
        model.addAttribute("jobId", id);
        return "employer/post-job";
    }

    @PostMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable Long id,
                          @Valid @ModelAttribute JobDto jobDto,
                          BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("jobDto", jobDto);
            model.addAttribute("jobId", id);
            return "employer/post-job";
        }
        try {
            jobService.updateJob(id, jobDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update job: " + e.getMessage());
        }
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJob(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Job deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/{id}/toggle-status")
    public String toggleJobStatus(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        jobService.toggleJobStatus(id, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Job status updated.");
        return "redirect:/employer/jobs";
    }

    // =============================================
    // APPLICANTS
    // =============================================
    @GetMapping("/jobs/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Job job = jobService.getJobById(jobId);
        List<Application> applications = applicationService.getJobApplications(jobId, userDetails.getUsername());
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "employer/applicants";
    }

    @PostMapping("/applications/{appId}/update-status")
    public String updateApplicationStatus(@PathVariable Long appId,
                                           @RequestParam String status,
                                           @RequestParam(required = false) String note,
                                           @RequestParam Long jobId,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        try {
            applicationService.updateApplicationStatus(appId, status, note, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success",
                    "Application status updated to " + status + ". Student notified via email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employer/jobs/" + jobId + "/applicants";
    }

    // =============================================
    // PROFILE
    // =============================================
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute User updatedUser,
                                RedirectAttributes redirectAttributes) {
        userService.updateProfile(userDetails.getUsername(), updatedUser);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/employer/profile";
    }
}
