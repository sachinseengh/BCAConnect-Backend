package com.bernhack.BCAConnect.dto.post;

import com.bernhack.BCAConnect.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostRequest {


    private Long post_id;
    private String subject;
    private String semester;
    private String caption;
    private String content;
    private String filename;
    private Boolean isNote;
    private String date;
    private User user;
}
