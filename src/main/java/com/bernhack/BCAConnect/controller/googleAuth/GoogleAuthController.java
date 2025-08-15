package com.bernhack.BCAConnect.controller.googleAuth;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.repository.RoleRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.impl.UserDetailsServiceImpl;
import com.bernhack.BCAConnect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {
            // Step 1: Exchange code for tokens
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("code", code);
            tokenParams.add("client_id", clientId);
            tokenParams.add("client_secret", clientSecret);
            tokenParams.add("redirect_uri", "http://localhost:8080/auth/google/callback");
            tokenParams.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, tokenRequest, Map.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to exchange code for token");
            }

            String idToken = (String) tokenResponse.getBody().get("id_token");

            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID token not found in response");
            }

            // Step 2: Get user info using id_token
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch user info");
            }

            Map<String, Object> userInfo = userInfoResponse.getBody();

            String email = (String) userInfo.get("email");
            String firstName = (String) userInfo.get("given_name");
            String lastName = (String) userInfo.get("family_name");

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found in Google user info");
            }

            // Step 3: Check user existence or register new user
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null ) {

                if (!"GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {

                    try {
                        String message = "Email already registered with "+user.getAuthProvider();
                        String encodedMessage = URLEncoder.encode(message, "UTF-8");
                        String redirectUrl = "http://localhost:5173/login?message=" + encodedMessage;

                        return ResponseEntity.status(HttpStatus.FOUND)
                                .location(URI.create(redirectUrl))
                                .build();
                    } catch (UnsupportedEncodingException e) {
                        // Handle encoding exception (unlikely to occur with UTF-8)
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }

            if (user == null) {
                user = new User();
                user.setAuthProvider("GOOGLE");
                user.setEmail(email);
                user.setFullName(firstName+" " + lastName);
                user.setSemester("Unknown");
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                Role role = roleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException("Role not found"));
                user.getRoles().add(role);
                userRepository.save(user);
            }

            // Step 4: Load user details and generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

            // Step 5: Redirect frontend with JWT token
            String redirectUrl = "http://localhost:5173/login?token=" + jwtToken;
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google auth failed");
        }
    }
}
