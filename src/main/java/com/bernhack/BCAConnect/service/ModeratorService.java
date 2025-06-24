package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.post.PostResponse;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Posts;

import java.util.List;

public interface ModeratorService {



    List<UserResponse> getAllUsers();

    Long deleteUser(String username);


    List<PostResponse> getAllUnverifiedPosts();


}
