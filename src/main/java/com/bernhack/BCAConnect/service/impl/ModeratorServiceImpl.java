package com.bernhack.BCAConnect.service.impl;


import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModeratorServiceImpl implements ModeratorService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {

       List<User> users = userRepository.findAll();


       List<UserResponse> responses = new ArrayList<>();
       for(User user:users){

           List<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());



        responses.add(new UserResponse(user.getId(),user.getFullName(),user.getEmail(),user.getUserName(),user.getSemester(),roles));
       }
        return responses;

    }

    @Override
    public Long deleteUser(String username) {

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("User not found"));
        userRepository.delete(user);
        return user.getId();
    }

    @Override
    public List<Posts> getAllUnverifiedPosts() {
        return null;
    }
}
