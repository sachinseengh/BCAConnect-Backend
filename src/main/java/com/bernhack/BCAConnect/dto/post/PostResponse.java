package com.bernhack.BCAConnect.dto.post;

import com.bernhack.BCAConnect.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {


    private Long id;
    private String caption;
    private String content;
    private String subject;
    private String semester;
    private LocalDateTime date;
    private UserResponse userResponse;
    private String fileUrl;
    private String fileType;
    private String filename;

}
