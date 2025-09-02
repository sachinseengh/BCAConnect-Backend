package com.bernhack.BCAConnect.service.impl;


import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.EmailService;
import com.bernhack.BCAConnect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public void sendVerificationEmail(String to, String sub ,String bdy) {

        String subject = sub;
        String body = bdy;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sachinseengh@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }


    @Override
    public ResponseEntity<?> resendverification(Map<String, String> request) {

        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("User already verified");
        }

        user.setTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(user);

        String verificationUrl = "http://localhost:5173/verify-email?token=" + token;
        String subject = "Verify your email";
        String body = "Click the link to verify your email:\n" + verificationUrl;

        sendVerificationEmail(email,subject,body);

        return ResponseEntity.ok("Verification email resent");
    }

    @Override
    public ResponseEntity<?> verifyEmail(String token) {

        String email = jwtUtil.extractUsername(token);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }

        User user = userOpt.get();

        if (user.isEnabled()) {
            return ResponseEntity.ok("Email already verified.");
        }

        Duration diff = Duration.between(user.getTokenCreatedAt(), LocalDateTime.now());
        if (diff.toMinutes() > 15) {
            return ResponseEntity.badRequest().body("Your verification token has expired");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("Email verified successfully. You can now login.");

    }

    @Override
    public ResponseEntity<?> sendForgetPasswordEmail(String email) {

        String userEmail= email;

        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();

        user.setForgetTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(user);

        String verificationUrl = "http://localhost:5173/change-password?token=" + token;
        String subject = "Forget password Email!";
        String body = "Click the link to change  your password:\n" + verificationUrl;

        sendVerificationEmail(email,subject,body);


        return ResponseEntity.ok("Verification email resent");
    }

    @Override
    public ResponseEntity<?> changeForgetPassword(String token) {


        String email = jwtUtil.extractUsername(token);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();


        LocalDateTime tokendate= user.getTokenCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        Duration diff = Duration.between(user.getForgetTokenCreatedAt(),LocalDateTime.now());

        if(diff.toMinutes() > 15){
            return ResponseEntity.badRequest().body("Forget password link Expired !");
        }


        user.setPassword(passwordEncoder.encode("BCACONNECT"));
        userRepository.save(user);

        return ResponseEntity.ok().body("BCACONNECT is your new password! Log in and Change your password");
    }
}
