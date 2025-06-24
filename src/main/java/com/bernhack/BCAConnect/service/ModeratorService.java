package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.user.UserResponse;

import java.util.List;

public interface ModeratorService {



    List<UserResponse> getAllUsers();
}
