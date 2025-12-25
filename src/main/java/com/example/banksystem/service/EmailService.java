package com.example.banksystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        if (mailSender == null) {
            log.warn(
                    "Email sending is not configured (no JavaMailSender bean). Skipping verification email to {}. Code: {}",
                    toEmail, code);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("odc.bank2025@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Email Təsdiqi");
        message.setText("Qeydiyyatı tamamlamak üçün kodunuz: " + code);

        // DEVELOPMENT ONLY: Log code to console so developer can see it even if email
        // fails
        log.info("============== VERIFICATION CODE: {} ==============", code);

        try {
            mailSender.send(message);
            log.info("Verification email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}. Code: {}", toEmail, code, e);
        }
    }
}
