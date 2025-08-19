package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.auth.LoginRequest;
import com.bernhack.BCAConnect.dto.auth.RegisterRequest;
import com.bernhack.BCAConnect.dto.changePassword.ChangePasswordRequest;
import com.bernhack.BCAConnect.dto.user.UserResponse;

public interface UserService {

 String login(LoginRequest loginRequest);

 String register(RegisterRequest registerRequest);

 UserResponse getUser(String userName);

 UserResponse getMe();

 Long deleteMe();

 String changePassword(ChangePasswordRequest changePasswordRequest);


}
