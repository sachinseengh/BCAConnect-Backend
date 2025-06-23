package com.bernhack.BCAConnect.service.impl;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.dto.auth.LoginRequest;
import com.bernhack.BCAConnect.dto.auth.RegisterRequest;
import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.repository.RoleRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.UserService;
import com.bernhack.BCAConnect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

     @Autowired
     private JwtUtil jwtUtil;



    @Override
    public String register(RegisterRequest registerRequest) {

        checkEmail(registerRequest.getEmail());
        checkUsername(registerRequest.getUserName());

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setSemester(registerRequest.getSemester());
        user.setSemester(registerRequest.getSemester());
        user.setUserName(registerRequest.getUserName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByName(RoleEnum.USER.toString()).orElseThrow(()->new AppException(StringConstant.ROLE_EXCEPTION));

        user.getRoles().add(role);

        userRepository.save(user);
        return StringConstant.REGISTERED_SUCCESSFULLY;
    }

    @Override
    public UserResponse getUser(String userName) {

        User user = userRepository.findByUserName(userName).orElseThrow(()->new AppException("User not found"));

        List<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

        List<Posts> posts = user.getPosts();

         return new UserResponse(user.getId(),user.getFullName(),user.getEmail(),user.getSemester(),roles,posts);

    }

    @Override
    public String login(LoginRequest loginRequest) {

        User user = userRepository.findByUserName(loginRequest.getUserName()).orElseThrow(()->new AppException("User not found"));


        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(),loginRequest.getPassword())
            );
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.getUserName());
            String jwt =jwtUtil.generateToken(userDetails.getUsername());
            return jwt;

        }catch (Exception e){

            throw new AppException(StringConstant.INVALID_CREDENTIALS);

        }
    }


    public void checkEmail(String email){

        if(userRepository.findByEmail(email).isPresent()){
            throw new AppException("Email Already Exists");
        }
    }

    public void checkUsername(String username){
        if(userRepository.findByUserName(username).isPresent()){
            throw new AppException("Username Already Exists");
        }
    }

}



