package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.user.UserResponse;

import java.util.List;

public interface AdminService {



    List<UserResponse> getAllModerators(String roleName);

    String makeModerator(String username);


    String removeModerator(String username);
}
