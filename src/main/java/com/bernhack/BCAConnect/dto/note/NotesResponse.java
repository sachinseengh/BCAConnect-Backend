package com.bernhack.BCAConnect.dto.note;


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
public class NotesResponse {

    private Long note_id;
    private LocalDateTime date;
    private UserResponse userResponse;
    private String semester;
    private String subject;
    private String fileUrl;
    private String fileType;
    private String fileName;
}
