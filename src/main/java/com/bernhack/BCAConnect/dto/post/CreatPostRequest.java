package com.bernhack.BCAConnect.dto.post;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatPostRequest {
    private String subject;
    private String semester;
    private String caption;
    private String content;
    private Boolean isNote;
    private LocalDateTime date;
}
