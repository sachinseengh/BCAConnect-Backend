package com.bernhack.BCAConnect.service.impl;


import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.post.PostResponse;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Notes;
import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.repository.NoteRepository;
import com.bernhack.BCAConnect.repository.PostRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModeratorServiceImpl implements ModeratorService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NoteRepository noteRepository;


    @Override
    public List<UserResponse> getAllUsers() {

       List<User> users = userRepository.findAll();


       List<UserResponse> responses = new ArrayList<>();
       for(User user:users){

           List<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());



        responses.add(new UserResponse(user.getId(),user.getFullName(),user.getEmail(),user.getUserName(),user.getSemester(),roles));
       }
        return responses;

    }

    @Override
    public Long deleteUser(String username) {

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("User not found"));
        userRepository.delete(user);
        return user.getId();
    }


    @Override
    public String postVerification(VerifyPostRequest verifyPostRequest) {

        Posts post = postRepository.findById(verifyPostRequest.getId()).orElseThrow(()->new AppException("Post Not Found"));

        if(verifyPostRequest.isVerifed()){

            User user = post.getUser();
            post.setIsVerifed(true);

            if(post.getIsNote()){
                Notes note = new Notes();
                note.setSemester(post.getSemester());
                note.setSubject(post.getSubject());
                note.setFileName(post.getFilename());
                note.setDate(LocalDateTime.now());
                note.setUser(user);
                noteRepository.save(note);
            }

            postRepository.save(post);

            return StringConstant.POST_VERIFIED;

        }else{
            postRepository.delete(post);
            return StringConstant.POST_DECLINED;
        }
    }

    @Override
    public List<PostResponse> getAllUnverifiedPosts() {

        List<Posts> posts = postRepository.getAllPosts().orElse(Collections.emptyList());

        List<PostResponse> responses = new ArrayList<>();

        if (posts != null) {
            for (Posts post : posts) {

                List<String> roles = post.getUser().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                UserResponse userResponse = new UserResponse(post.getUser()
                        .getId(), post.getUser().getFullName(), post.getUser().getEmail(), post.getUser().getUserName(), post.getUser().getSemester(), roles);

                String fileUrl = null;
                String fileType = null;
                String fileName = null;
                if (post.getFilename() != null) {
                    fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(post.getFilename())
                            .toUriString();
                    fileType = post.getFileType();
                    fileName = post.getFilename();


                }

                responses.add(new PostResponse(post.getId(), post.getCaption(), post.getContent(), post.getSubject(), post.getSemester(), post.getDate(), userResponse, fileUrl, fileType, fileName));
            }
            return responses;
        }
        return responses;
    }
}
