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
            log.warn("Email sending is not configured (no JavaMailSender bean). Skipping verification email to {}. Code: {}", toEmail, code);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Təsdiqi");
        message.setText("Qeydiyyatı tamamlamak üçün kodunuz: " + code);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {}. Code: {}. Error: {}", toEmail, code, e.toString());
        }
    }
}
