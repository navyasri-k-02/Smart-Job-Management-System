package com.jobportal.controller;

import com.jobportal.dto.RegisterDto;
import com.jobportal.entity.OtpToken;
import com.jobportal.entity.User;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =============================================
    // HOME
    // =============================================
    @GetMapping("/")
    public String home() {
        return "redirect:/jobs";
    }

    // =============================================
    // LOGIN
    // =============================================
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password, or account not verified.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "auth/login";
    }

    // =============================================
    // REGISTER
    // =============================================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registerDto", dto);
            return "auth/register";
        }

        if (dto.getRole().equals("EMPLOYER") &&
                (dto.getCompanyName() == null || dto.getCompanyName().isBlank())) {
            model.addAttribute("error", "Company name is required for employers.");
            model.addAttribute("registerDto", dto);
            return "auth/register";
        }

        try {
            User user = userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("email", user.getEmail());
            redirectAttributes.addFlashAttribute("message",
                    "Registration successful! Check your email for OTP verification.");
            return "redirect:/verify-otp";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDto", dto);
            return "auth/register";
        }
    }

    // =============================================
    // OTP VERIFICATION
    // =============================================
    @GetMapping("/verify-otp")
    public String verifyOtpPage(Model model) {
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            RedirectAttributes redirectAttributes) {
        boolean verified = userService.verifyOtp(email, otp, OtpToken.OtpPurpose.EMAIL_VERIFICATION);
        if (verified) {
            redirectAttributes.addFlashAttribute("message",
                    "Email verified successfully! You can now log in.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email,
                            @RequestParam(defaultValue = "EMAIL_VERIFICATION") String purpose,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.resendOtp(email, OtpToken.OtpPurpose.valueOf(purpose));
            redirectAttributes.addFlashAttribute("message", "OTP resent to " + email);
            redirectAttributes.addFlashAttribute("email", email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (purpose.equals("PASSWORD_RESET")) {
            return "redirect:/reset-password";
        }
        return "redirect:/verify-otp";
    }

    // =============================================
    // FORGOT PASSWORD
    // =============================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendResetOtp(@RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.sendPasswordResetOtp(email);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("message",
                    "Password reset OTP sent to " + email);
            return "redirect:/reset-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String otp,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }

        boolean success = userService.resetPassword(email, otp, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("message",
                    "Password reset successfully! Please log in.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
    }

    // =============================================
    // ACCESS DENIED
    // =============================================
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
