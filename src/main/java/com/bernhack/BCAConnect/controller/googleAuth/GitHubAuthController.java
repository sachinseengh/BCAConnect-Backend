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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/github")
public class GitHubAuthController {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> githubCallback(@RequestParam String code) {
        try {
            // Step 1: Exchange code for access token
            String tokenUrl = "https://github.com/login/oauth/access_token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            tokenHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("client_id", clientId);
            tokenParams.add("client_secret", clientSecret);
            tokenParams.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token exchange failed");
            }

            // Step 2: Fetch GitHub user profile
            HttpHeaders profileHeaders = new HttpHeaders();
            profileHeaders.setBearerAuth(accessToken);
            profileHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);

            ResponseEntity<Map> profileResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    profileRequest,
                    Map.class
            );

            Map<String, Object> profile = profileResponse.getBody();
            String email = (String) profile.get("email");
            String username = (String) profile.get("login");
            String name = (String) profile.get("name");

            // Step 3: Fallback to fetch public emails
            if (email == null) {
                ResponseEntity<List> emailResponse = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        profileRequest,
                        List.class
                );
                List<Map<String, Object>> emails = emailResponse.getBody();
                for (Map<String, Object> e : emails) {
                    if (Boolean.TRUE.equals(e.get("primary"))) {
                        email = (String) e.get("email");
                        break;
                    }
                }
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not available from GitHub.");
            }

            // Step 4: Handle login or registration
            User user = userRepository.findByEmail(email).orElse(null);
            UserDetails userDetails;

            if (user != null ) {

                if (!"GITHUB".equalsIgnoreCase(user.getAuthProvider())) {

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

            } else {
                user = new User();
                user.setAuthProvider("GITHUB");
                user.setEmail(email);
                user.setFullName(name);
                user.setSemester("Unknown");
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                Role role = roleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException("Role not found"));
                user.getRoles().add(role);
                userRepository.save(user);
            }

            // Step 5: Load user & generate JWT
            userDetails = userDetailsService.loadUserByUsername(email);
            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:5173/login?token=" + jwtToken))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("GitHub auth failed");
        }
    }
}
