package com.bernhack.BCAConnect.dto.post;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CreatPostRequest {
    private String subject;
    private String semester;
    private String caption;
    private String content;
    private Boolean isNote;
    private LocalDateTime date;

}
