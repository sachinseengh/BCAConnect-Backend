package com.bernhack.BCAConnect.dto.changePassword;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor

public class ChangePasswordRequest {

    private String oldPassword;

    private String newPassword;

}
