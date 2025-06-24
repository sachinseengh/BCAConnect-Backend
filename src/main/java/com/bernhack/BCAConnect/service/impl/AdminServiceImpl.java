package com.bernhack.BCAConnect.service.impl;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.repository.RoleRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<UserResponse> getAllModerators(String roleName) {

        List<User> moderators = userRepository.findModerators(RoleEnum.MODERATOR.name()).orElse(Collections.emptyList());

        List<UserResponse> responses = new ArrayList<>();
        for (User user : moderators) {

            List<String> roles = user.getRoles().stream().map(role ->
                role.getName()).collect(Collectors.toList());

            responses.add(new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getUserName(), user.getSemester(), roles));
        }
        return responses;
    }

    @Override
    public String makeModerator(String username) {

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("Username not found"));

        Role role = roleRepository.findByName(RoleEnum.MODERATOR.name()).orElseThrow(()-> new AppException("Role not Found"));

        user.getRoles().add(role);

        return StringConstant.MODERATOR_MADE;
    }

    @Override
    public String removeModerator(String username) {

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("User not Found"));

        Role role = roleRepository.findByName(RoleEnum.MODERATOR.name()).orElseThrow(()->new AppException("Role not found"));

        user.getRoles().remove(role);
        return "";
    }


}
