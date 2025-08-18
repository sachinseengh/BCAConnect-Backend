package com.bernhack.BCAConnect.service.impl;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.changePassword.ChangePasswordRequest;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.dto.auth.LoginRequest;
import com.bernhack.BCAConnect.dto.auth.RegisterRequest;
import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.repository.RoleRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.EmailService;
import com.bernhack.BCAConnect.service.UserService;
import com.bernhack.BCAConnect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private EmailService emailService;


    @Override
    public String register(RegisterRequest registerRequest) {


        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            String message = "Username already exists with provider: " + existingUser.get().getAuthProvider();
            throw new AppException(message);  // This will be caught by your GlobalExceptionHandler
        }



        User user = new User();
        user.setAuthProvider("MANUAL");
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setSemester(registerRequest.getSemester());
        user.setSemester(registerRequest.getSemester());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByName(RoleEnum.USER.toString()).orElseThrow(()->new AppException(StringConstant.ROLE_EXCEPTION));

        user.getRoles().add(role);


        //Email Verification
        user.setEnabled(false);
        user.setTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        //Sending Email
        String token = jwtUtil.generateVerificationToken(user);

        String verificationUrl = "http://localhost:8080/auth/verify-email?token=" + token;
        String subject = "Verify your email";
        String body = "Click the link to verify your email:\n" + verificationUrl;

        emailService.sendVerificationEmail(user.getEmail(),subject,body);

        return StringConstant.REGISTERED_SUCCESSFULLY;
    }

    @Override
    public String login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail().trim();

        User user = userRepository.findByEmail(email).orElseThrow(()->new AppException("User not found"));

        if (!user.isEnabled()) {
            Duration diff = Duration.between(user.getTokenCreatedAt(), LocalDateTime.now());

            if (diff.toMinutes() > 15) {
                throw new AppException("TokenExpired"); // frontend will handle this specifically
            }
            throw new AppException("Email not verified");
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(),loginRequest.getPassword())
            );
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.getEmail());
            String jwt =jwtUtil.generateToken(userDetails.getUsername());
            return jwt;

        }catch (Exception e){

            throw new AppException(StringConstant.INVALID_CREDENTIALS);

        }
    }


    @Override
    public UserResponse getUser(String userName) {

        User user = userRepository.findByEmail(userName).orElseThrow(()->new AppException("User not found"));

        List<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

         return new UserResponse(user.getId(),user.getFullName(),user.getEmail(),user.getSemester(),roles);

    }

    @Override
    public UserResponse getMe() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return getUser(username);
    }


    @Override
    public Long deleteMe() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByEmail(username).orElseThrow(()->new AppException("User not found"));
        userRepository.delete(user);
        return user.getId();

    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new AppException("User not found"));


        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(),user.getPassword())){
            throw new AppException("Incorrect Old Password");
        }

          user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

          userRepository.save(user);
        return StringConstant.PASSWORD_CHANGED;
    }





}



