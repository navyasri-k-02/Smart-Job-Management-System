package com.jobportal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // =============================================
    // SEND OTP VERIFICATION EMAIL
    // =============================================
    @Async
    public void sendOtpVerificationEmail(String toEmail, String fullName, String otp) {
        String subject = "Verify Your Email - Smart Job Portal";
        String body = buildOtpEmailTemplate(fullName, otp, "Email Verification",
                "Use this OTP to verify your email address. It expires in 10 minutes.");
        sendHtmlEmail(toEmail, subject, body);
    }

    // =============================================
    // SEND PASSWORD RESET OTP
    // =============================================
    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String otp) {
        String subject = "Password Reset OTP - Smart Job Portal";
        String body = buildOtpEmailTemplate(fullName, otp, "Password Reset",
                "Use this OTP to reset your password. It expires in 10 minutes. If you did not request this, ignore this email.");
        sendHtmlEmail(toEmail, subject, body);
    }

    // =============================================
    // SEND WELCOME EMAIL
    // =============================================
    @Async
    public void sendWelcomeEmail(String toEmail, String fullName, String role) {
        String subject = "Welcome to Smart Job Portal! \uD83C\uDF89";
        String roleMessage = role.equals("STUDENT")
                ? "Start exploring thousands of job opportunities tailored for you!"
                : "Start posting jobs and find the perfect candidates for your team!";

        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background:#f4f6f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                        <!-- Header -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#667eea,#764ba2);padding:40px 40px 30px;text-align:center;">
                            <h1 style="color:#fff;margin:0;font-size:28px;font-weight:700;">\uD83D\uDE80 Smart Job Portal</h1>
                            <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:15px;">Connecting Talent with Opportunity</p>
                          </td>
                        </tr>
                        <!-- Body -->
                        <tr>
                          <td style="padding:40px;">
                            <h2 style="color:#1a1a2e;font-size:24px;margin:0 0 16px;">Welcome aboard, %s! \uD83D\uDC4B</h2>
                            <p style="color:#555;font-size:16px;line-height:1.7;margin:0 0 20px;">
                              Your account has been successfully created and verified.
                            </p>
                            <div style="background:#f0f4ff;border-left:4px solid #667eea;border-radius:8px;padding:20px;margin:24px 0;">
                              <p style="color:#4a5568;font-size:15px;margin:0;line-height:1.6;">%s</p>
                            </div>
                            <a href="%s/login" style="display:inline-block;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;text-decoration:none;padding:14px 32px;border-radius:50px;font-size:15px;font-weight:600;margin:16px 0;">
                              Get Started Now &rarr;
                            </a>
                          </td>
                        </tr>
                        %s
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(fullName, roleMessage, baseUrl, buildFooter());

        sendHtmlEmail(toEmail, subject, body);
    }

    // =============================================
    // SEND JOB APPLICATION CONFIRMATION
    // =============================================
    @Async
    public void sendApplicationConfirmationEmail(String toEmail, String studentName,
                                                  String jobTitle, String companyName) {
        String subject = "Application Submitted - " + jobTitle + " at " + companyName;
        String body = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#f4f6f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                        <tr>
                          <td style="background:linear-gradient(135deg,#667eea,#764ba2);padding:40px;text-align:center;">
                            <h1 style="color:#fff;margin:0;font-size:28px;">\uD83D\uDE80 Smart Job Portal</h1>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:40px;">
                            <div style="text-align:center;margin-bottom:28px;">
                              <div style="width:70px;height:70px;background:#e8f5e9;border-radius:50%%;display:inline-flex;align-items:center;justify-content:center;font-size:32px;">&#9989;</div>
                            </div>
                            <h2 style="color:#1a1a2e;font-size:22px;text-align:center;margin:0 0 16px;">Application Submitted!</h2>
                            <p style="color:#555;font-size:15px;line-height:1.7;">Hi <strong>%s</strong>,</p>
                            <p style="color:#555;font-size:15px;line-height:1.7;">
                              Your application for <strong>%s</strong> at <strong>%s</strong> has been successfully submitted.
                            </p>
                            <div style="background:#f8f9ff;border:1px solid #e2e8f0;border-radius:10px;padding:20px;margin:24px 0;">
                              <table width="100%%">
                                <tr>
                                  <td style="color:#718096;font-size:13px;padding:6px 0;">&#128203; Position</td>
                                  <td style="color:#2d3748;font-size:14px;font-weight:600;padding:6px 0;">%s</td>
                                </tr>
                                <tr>
                                  <td style="color:#718096;font-size:13px;padding:6px 0;">&#127970; Company</td>
                                  <td style="color:#2d3748;font-size:14px;font-weight:600;padding:6px 0;">%s</td>
                                </tr>
                                <tr>
                                  <td style="color:#718096;font-size:13px;padding:6px 0;">&#128204; Status</td>
                                  <td style="color:#f6a609;font-size:14px;font-weight:600;padding:6px 0;">&#9203; Pending Review</td>
                                </tr>
                              </table>
                            </div>
                            <p style="color:#555;font-size:14px;line-height:1.7;">
                              We'll notify you when the employer reviews your application. Good luck! &#129310;
                            </p>
                            <a href="%s/student/applications" style="display:inline-block;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;text-decoration:none;padding:12px 28px;border-radius:50px;font-size:14px;font-weight:600;">
                              Track Your Application &rarr;
                            </a>
                          </td>
                        </tr>
                        %s
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(studentName, jobTitle, companyName, jobTitle, companyName, baseUrl, buildFooter());

        sendHtmlEmail(toEmail, subject, body);
    }

    // =============================================
    // SEND APPLICATION STATUS UPDATE
    // =============================================
    @Async
    public void sendApplicationStatusUpdateEmail(String toEmail, String studentName,
                                                   String jobTitle, String companyName,
                                                   String status, String note) {
        boolean isPositive = status.equals("SHORTLISTED") || status.equals("HIRED");
        String emoji = switch (status) {
            case "SHORTLISTED" -> "\uD83C\uDF89";
            case "HIRED" -> "\uD83E\uDD73";
            case "REJECTED" -> "\uD83D\uDE14";
            default -> "\uD83D\uDCCB";
        };
        String statusColor = isPositive ? "#27ae60" : "#e74c3c";
        String statusLabel = status.substring(0, 1) + status.substring(1).toLowerCase();

        String subject = emoji + " Application Update: " + statusLabel + " - " + jobTitle;
        String noteSection = (note != null && !note.isBlank())
                ? "<div style='background:#fffbf0;border-left:4px solid #f6a609;border-radius:8px;padding:16px;margin:20px 0;'>" +
                  "<p style='color:#555;font-size:14px;margin:0;'><strong>Employer's Note:</strong> " + note + "</p></div>"
                : "";

        String body = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#f4f6f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                        <tr>
                          <td style="background:linear-gradient(135deg,#667eea,#764ba2);padding:40px;text-align:center;">
                            <h1 style="color:#fff;margin:0;font-size:28px;">\uD83D\uDE80 Smart Job Portal</h1>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:40px;">
                            <div style="text-align:center;margin-bottom:24px;">
                              <span style="font-size:48px;">%s</span>
                            </div>
                            <h2 style="color:#1a1a2e;font-size:22px;text-align:center;margin:0 0 20px;">Application Status Updated</h2>
                            <p style="color:#555;font-size:15px;line-height:1.7;">Hi <strong>%s</strong>,</p>
                            <p style="color:#555;font-size:15px;line-height:1.7;">
                              Your application for <strong>%s</strong> at <strong>%s</strong> has been updated.
                            </p>
                            <div style="text-align:center;margin:24px 0;">
                              <span style="background:%s;color:#fff;padding:10px 28px;border-radius:50px;font-size:16px;font-weight:700;">%s %s</span>
                            </div>
                            %s
                            <a href="%s/student/applications" style="display:inline-block;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;text-decoration:none;padding:12px 28px;border-radius:50px;font-size:14px;font-weight:600;">
                              View Application Details &rarr;
                            </a>
                          </td>
                        </tr>
                        %s
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(emoji, studentName, jobTitle, companyName, statusColor, emoji, statusLabel, noteSection, baseUrl, buildFooter());

        sendHtmlEmail(toEmail, subject, body);
    }

    // =============================================
    // PRIVATE HELPERS
    // =============================================

    private String buildOtpEmailTemplate(String fullName, String otp, String purpose, String message) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#f4f6f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                        <tr>
                          <td style="background:linear-gradient(135deg,#667eea,#764ba2);padding:40px;text-align:center;">
                            <h1 style="color:#fff;margin:0;font-size:28px;">\uD83D\uDE80 Smart Job Portal</h1>
                            <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:14px;">%s</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:40px;">
                            <h2 style="color:#1a1a2e;font-size:22px;margin:0 0 16px;">Hi %s,</h2>
                            <p style="color:#555;font-size:15px;line-height:1.7;margin:0 0 28px;">%s</p>
                            <div style="text-align:center;margin:32px 0;">
                              <div style="display:inline-block;background:linear-gradient(135deg,#667eea,#764ba2);border-radius:16px;padding:24px 48px;">
                                <p style="color:rgba(255,255,255,0.8);font-size:12px;margin:0 0 8px;letter-spacing:3px;text-transform:uppercase;">Your OTP</p>
                                <p style="color:#fff;font-size:42px;font-weight:800;margin:0;letter-spacing:12px;">%s</p>
                              </div>
                            </div>
                            <p style="color:#999;font-size:13px;text-align:center;margin:16px 0 0;">&#9201; This code expires in <strong>10 minutes</strong></p>
                            <p style="color:#e74c3c;font-size:13px;text-align:center;margin:8px 0;">Never share this OTP with anyone.</p>
                          </td>
                        </tr>
                        %s
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(purpose, fullName, message, otp, buildFooter());
    }

    private String buildFooter() {
        return """
                <tr>
                  <td style="background:#f8f9fa;padding:24px 40px;text-align:center;border-top:1px solid #e2e8f0;">
                    <p style="color:#999;font-size:12px;margin:0 0 4px;">&copy; 2025 Smart Job Portal. All rights reserved.</p>
                    <p style="color:#bbb;font-size:11px;margin:0;">If you didn't request this email, please ignore it.</p>
                  </td>
                </tr>
                """;
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Smart Job Portal");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", toEmail, e.getMessage());
        }
    }
}
