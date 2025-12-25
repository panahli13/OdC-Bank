package com.example.banksystem.controller;

import com.example.banksystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/email")
    public String testEmail(@RequestParam String to) {
        try {
            emailService.sendVerificationEmail(to, "123456");
            return "Email göndərilmə sorğusu icra olundu. Log-ları yoxlayın.";
        } catch (Exception e) {
            return "Xəta baş verdi: " + e.getMessage();
        }
    }
}
