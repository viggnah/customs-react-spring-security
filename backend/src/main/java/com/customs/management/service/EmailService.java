package com.customs.management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.from:noreply@customs.gov}")
    private String fromEmail;
    
    // Mock implementation for password reset email
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        // In a real implementation, you would use Spring Mail here
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        
        System.out.println("=== PASSWORD RESET EMAIL ===");
        System.out.println("To: " + toEmail);
        System.out.println("From: " + fromEmail);
        System.out.println("Subject: Customs Authority - Password Reset Request");
        System.out.println("Content:");
        System.out.println("Dear " + username + ",");
        System.out.println();
        System.out.println("You have requested to reset your password for the Customs Management System.");
        System.out.println("Please click on the following link to reset your password:");
        System.out.println();
        System.out.println(resetLink);
        System.out.println();
        System.out.println("This link will expire in 24 hours.");
        System.out.println();
        System.out.println("If you did not request this password reset, please ignore this email.");
        System.out.println();
        System.out.println("Best regards,");
        System.out.println("Customs Authority System Team");
        System.out.println("============================");
        
        // TODO: Replace with actual email sending implementation
        // Example with Spring Mail:
        /*
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject("Customs Authority - Password Reset Request");
        message.setText(buildEmailContent(username, resetLink));
        javaMailSender.send(message);
        */
    }
    
    public void sendWelcomeEmail(String toEmail, String username, String temporaryPassword) {
        System.out.println("=== WELCOME EMAIL ===");
        System.out.println("To: " + toEmail);
        System.out.println("From: " + fromEmail);
        System.out.println("Subject: Welcome to Customs Management System");
        System.out.println("Content:");
        System.out.println("Dear " + username + ",");
        System.out.println();
        System.out.println("Welcome to the Customs Management System!");
        System.out.println("Your account has been created successfully.");
        System.out.println();
        System.out.println("Username: " + username);
        System.out.println("Temporary Password: " + temporaryPassword);
        System.out.println();
        System.out.println("Please log in and change your password immediately.");
        System.out.println("Login URL: " + frontendUrl + "/login");
        System.out.println();
        System.out.println("Best regards,");
        System.out.println("Customs Authority System Team");
        System.out.println("======================");
    }
}
