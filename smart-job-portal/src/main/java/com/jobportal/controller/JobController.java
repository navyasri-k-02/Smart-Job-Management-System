package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public String listJobs(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String location,
                           @RequestParam(required = false) String jobType,
                           @RequestParam(required = false) String industry,
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
        return "public/jobs";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);
        return "public/job-detail";
    }
}
