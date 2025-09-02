package com.bernhack.BCAConnect.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface EmailService {

    public void sendVerificationEmail(String to,String sub,String bdy);


    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token);

    public ResponseEntity<?> resendverification(@RequestBody Map<String,String> request);


    public ResponseEntity<?> sendForgetPasswordEmail(@RequestBody String email);

    public ResponseEntity<?> changeForgetPassword(@RequestParam("token") String token);
}
