package com.bernhack.BCAConnect.controller.auth;


import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.dto.auth.LoginRequest;
import com.bernhack.BCAConnect.dto.auth.RegisterRequest;
import com.bernhack.BCAConnect.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private  UserService userService;

    @PostMapping("/register")
    public ResponseEntity<GlobalAPIResponse> register(@RequestBody RegisterRequest registerRequest){
        return successResponse(StringConstant.REGISTERED_SUCCESSFULLY,userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalAPIResponse> login(@RequestBody LoginRequest loginRequest){
        return successResponse(StringConstant.LOGIN_SUCCESSFULLY,userService.login(loginRequest));
    }
}
