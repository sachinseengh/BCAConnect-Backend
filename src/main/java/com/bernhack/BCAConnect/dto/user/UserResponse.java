package com.bernhack.BCAConnect.dto.user;


import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

private Long id;
private String fullName;
private String email;
private String userName;
private String semester;
private List<String> roles;
private List<Posts> posts;
}
