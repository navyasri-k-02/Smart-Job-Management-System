package com.jobportal.service;

import com.jobportal.dto.RegisterDto;
import com.jobportal.entity.OtpToken;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.OtpTokenRepository;
import com.jobportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       OtpTokenRepository otpTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =============================================
    // SPRING SECURITY USER DETAILS
    // =============================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .accountLocked(!user.isEnabled())
                .build();
    }

    // =============================================
    // REGISTRATION
    // =============================================
    @Transactional
    public User registerUser(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.valueOf(dto.getRole()))
                .enabled(false)
                .companyName(dto.getCompanyName())
                .build();

        user = userRepository.save(user);

        // Send OTP
        String otp = generateAndSaveOtp(user.getEmail(), OtpToken.OtpPurpose.EMAIL_VERIFICATION);
        emailService.sendOtpVerificationEmail(user.getEmail(), user.getFullName(), otp);

        log.info("User registered: {}", user.getEmail());
        return user;
    }

    // =============================================
    // OTP VERIFICATION
    // =============================================
    @Transactional
    public boolean verifyOtp(String email, String otp, OtpToken.OtpPurpose purpose) {
        Optional<OtpToken> tokenOpt = otpTokenRepository
                .findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(email, purpose);

        if (tokenOpt.isEmpty()) return false;

        OtpToken token = tokenOpt.get();
        if (token.isExpired() || !token.getOtp().equals(otp)) return false;

        token.setUsed(true);
        otpTokenRepository.save(token);

        if (purpose == OtpToken.OtpPurpose.EMAIL_VERIFICATION) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            user.setEnabled(true);
            userRepository.save(user);
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), user.getRole().name());
        }

        return true;
    }

    // =============================================
    // FORGOT PASSWORD
    // =============================================
    @Transactional
    public void sendPasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email."));

        String otp = generateAndSaveOtp(email, OtpToken.OtpPurpose.PASSWORD_RESET);
        emailService.sendPasswordResetEmail(email, user.getFullName(), otp);
    }

    @Transactional
    public boolean resetPassword(String email, String otp, String newPassword) {
        boolean valid = verifyOtp(email, otp, OtpToken.OtpPurpose.PASSWORD_RESET);
        if (!valid) return false;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    // =============================================
    // PROFILE
    // =============================================
    @Transactional
    public User updateProfile(String email, User updatedData) {
        User user = getUserByEmail(email);
        user.setFullName(updatedData.getFullName());
        user.setPhone(updatedData.getPhone());
        user.setLocation(updatedData.getLocation());
        user.setBio(updatedData.getBio());
        user.setSkills(updatedData.getSkills());
        user.setExperience(updatedData.getExperience());
        user.setEducation(updatedData.getEducation());
        user.setCompanyName(updatedData.getCompanyName());
        user.setCompanyWebsite(updatedData.getCompanyWebsite());
        user.setCompanyDescription(updatedData.getCompanyDescription());
        user.setCompanySize(updatedData.getCompanySize());
        user.setIndustry(updatedData.getIndustry());
        return userRepository.save(user);
    }

    @Transactional
    public void updateResumePath(String email, String path) {
        User user = getUserByEmail(email);
        user.setResumePath(path);
        userRepository.save(user);
    }

    // =============================================
    // ADMIN
    // =============================================
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // =============================================
    // HELPERS
    // =============================================
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public long countStudents() { return userRepository.countStudents(); }
    public long countEmployers() { return userRepository.countEmployers(); }

    private String generateAndSaveOtp(String email, OtpToken.OtpPurpose purpose) {
        // Delete old OTPs for this email+purpose
        otpTokenRepository.deleteByEmailAndPurpose(email, purpose);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new SecureRandom().nextInt(1000000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        otpTokenRepository.save(token);
        return otp;
    }

    @Transactional
    public void resendOtp(String email, OtpToken.OtpPurpose purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String otp = generateAndSaveOtp(email, purpose);
        if (purpose == OtpToken.OtpPurpose.EMAIL_VERIFICATION) {
            emailService.sendOtpVerificationEmail(email, user.getFullName(), otp);
        } else {
            emailService.sendPasswordResetEmail(email, user.getFullName(), otp);
        }
    }
}
