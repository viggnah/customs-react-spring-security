package com.tra.customs.service;

import com.tra.customs.entity.User;
import com.tra.customs.entity.PasswordResetToken;
import com.tra.customs.repository.UserRepository;
import com.tra.customs.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameWithRolesAndAuthorities(username);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    public String createPasswordResetToken(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with username: " + username);
        }
        
        User user = userOptional.get();
        
        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);
        
        // Create new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // 24 hours expiry
        
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        
        // Send email (mock implementation for now)
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
        
        return token;
    }
    
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        
        if (resetTokenOptional.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = resetTokenOptional.get();
        return !resetToken.isExpired() && !resetToken.getUsed();
    }
    
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        
        if (resetTokenOptional.isEmpty()) {
            throw new RuntimeException("Invalid password reset token");
        }
        
        PasswordResetToken resetToken = resetTokenOptional.get();
        
        if (resetToken.isExpired() || resetToken.getUsed()) {
            throw new RuntimeException("Password reset token has expired or already been used");
        }
        
        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
    
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiryDateLessThan(LocalDateTime.now());
    }
}
