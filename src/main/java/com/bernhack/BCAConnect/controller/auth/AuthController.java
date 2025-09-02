package com.bernhack.BCAConnect.controller.auth;


import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.dto.auth.LoginRequest;
import com.bernhack.BCAConnect.dto.auth.RegisterRequest;
import com.bernhack.BCAConnect.service.EmailService;
import com.bernhack.BCAConnect.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private  UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<GlobalAPIResponse> register(@RequestBody RegisterRequest registerRequest){
        return successResponse(StringConstant.REGISTERED_SUCCESSFULLY,userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalAPIResponse> login(@RequestBody LoginRequest loginRequest){
        return successResponse(StringConstant.LOGIN_SUCCESSFULLY,userService.login(loginRequest));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token){
        return emailService.verifyEmail(token);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String> request) {
        return emailService.resendverification(request);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> sendForgetPasswordEmail(@RequestBody String email){
        return emailService.sendForgetPasswordEmail(email);
    }

    @GetMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam("token") String token){
        return emailService.changeForgetPassword(token);
    }
}
