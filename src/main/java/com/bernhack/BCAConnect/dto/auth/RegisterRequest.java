package com.bernhack.BCAConnect.dto.auth;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    private String fullName;

    private String semester;

    private String email;

    private String password;
}
