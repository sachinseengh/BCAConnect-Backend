package com.bernhack.BCAConnect.dto.post;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPostRequest {

    private Long id;
    private boolean isVerifed;
}
