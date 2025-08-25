package com.bernhack.BCAConnect.service.impl;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.post.PostResponse;
import com.bernhack.BCAConnect.dto.post.UpdatePostRequest;
import com.bernhack.BCAConnect.dto.post.CreatPostRequest;

import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Notes;
import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.repository.NoteRepository;
import com.bernhack.BCAConnect.repository.PostRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import com.bernhack.BCAConnect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private NoteRepository noteRepository;

    @Override
    public String createPost(MultipartFile file ,CreatPostRequest creatPostRequest) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByEmail(username).orElseThrow(()->new AppException("User not found"));

        Posts post = new Posts();

        post.setSubject(creatPostRequest.getSubject());
        post.setCaption(creatPostRequest.getCaption());
        post.setSemester(creatPostRequest.getSemester());
        post.setContent(creatPostRequest.getContent());
        post.setIsNote(creatPostRequest.getIsNote());
        post.setIsVerified(false);
        post.setDate(LocalDateTime.now());

        post.setUser(user);

        String fileName=null;
        String fileType=null;
        if (file != null && !file.isEmpty()) {
            fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            post.setFilename(fileName);
            post.setFileType(file.getContentType()); // Save file type

        }

        postRepository.save(post);

        return StringConstant.POSTSAVED;
    }

    @Override
    @Transactional
    public String updatePost(UpdatePostRequest updatePostRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Posts post = postRepository.findById(updatePostRequest.getPost_id()).orElseThrow(()->new AppException("Post Not Found"));

        User user = userRepository.findByEmail(username).orElseThrow(()->new AppException("User not found"));


        if(updatePostRequest.getSubject() !=null || !(updatePostRequest.getSubject().equals(""))){
            post.setSubject(updatePostRequest.getSubject());
        }

        if(updatePostRequest.getSemester() !=null || !(updatePostRequest.getSemester().equals(""))){
            post.setSemester(updatePostRequest.getSemester());
        }

        if(updatePostRequest.getCaption() !=null || !(updatePostRequest.getCaption().equals(""))){
            post.setCaption(updatePostRequest.getCaption());
        }

        if(updatePostRequest.getContent() !=null || !(updatePostRequest.getContent().equals(""))){
            post.setCaption(updatePostRequest.getContent());
        }

        if(updatePostRequest.getFilename() !=null || !(updatePostRequest.getFilename().equals(""))){
            post.setFilename(updatePostRequest.getFilename());
        }

        post.setIsNote(updatePostRequest.getIsNote());
        post.setIsVerified(false);
        post.setDate(LocalDateTime.now());


        if(updatePostRequest.getIsNote()){
            Notes note = new Notes();

            if(updatePostRequest.getSubject() !=null || !(updatePostRequest.getSubject().equals(""))){
                note.setSubject(updatePostRequest.getSubject());
            }

            if(updatePostRequest.getSemester() !=null || !(updatePostRequest.getSemester().equals(""))){
                note.setSemester(updatePostRequest.getSemester());
            }

            if(updatePostRequest.getFilename() !=null || !(updatePostRequest.getFilename().equals(""))){
                note.setFileName(updatePostRequest.getFilename());
            }
            note.setDate(LocalDateTime.now());
            note.setUser(user);

            noteRepository.save(note);
        }


        postRepository.save(post);

        return StringConstant.POST_UPDATED;
    }

    @Override
    public String deletePost(Long id) {

        Posts post = postRepository.findById(id).orElseThrow(()->new AppException("Post Not Found"));


        postRepository.delete(post);


        return StringConstant.POST_DELETED;
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Posts> posts = postRepository.getAllPosts().orElse(Collections.emptyList());
//
        List<PostResponse> responses = new ArrayList<>();

        if (posts != null) {
            for (Posts post : posts) {

                List<String> roles = post.getUser().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                UserResponse userResponse = new UserResponse(post.getUser()
                        .getId(), post.getUser().getFullName(),post.getUser().getEmail(),post.getUser().getSemester(), roles);

                String fileUrl = null;
                String fileType = null;
                String fileName=null;
                if (post.getFilename() != null) {
                    fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(post.getFilename())
                            .toUriString();
                    fileType = post.getFileType();
                    fileName=post.getFilename();


                }

                responses.add(new PostResponse(post.getId(), post.getCaption(), post.getContent(), post.getSubject(),post.getSemester(),post.getDate(), userResponse, fileUrl, fileType,fileName));
            }
            return responses;
        }
        return responses;
    }

    @Override
    public List<PostResponse> getUserPost(String username) {

        User user = userRepository.findByEmail(username).orElseThrow(() -> new AppException("User not found"));

        List<Posts> posts = postRepository.findAllByUserOrderByDateDesc(user).orElse(Collections.emptyList());
        List<PostResponse> responses = new ArrayList<>();

        if (posts != null) {
            for (Posts post : posts) {

                List<String> roles = post.getUser().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                UserResponse userResponse = new UserResponse(post.getUser()
                        .getId(), post.getUser().getFullName(),post.getUser().getEmail(),post.getUser().getSemester(), roles);

                String fileUrl = null;
                String fileType = null;
                String fileName =null;

                if (post.getFilename() != null) {
                    fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(post.getFilename())
                            .toUriString();
                    fileType = post.getFileType();
                }

                responses.add(new PostResponse(post.getId(), post.getCaption(), post.getContent(), post.getSubject(),post.getSemester(),post.getDate(), userResponse, fileUrl, fileType,fileName));
            }
            return responses;
        } else {
            return responses;
        }
    }


    @Override
    public List<PostResponse> getUserSavedPost(){

     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

     User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new AppException("User not found"));


     List<Posts> savedPosts = postRepository.findAllSavedPost(user.getId()).orElse(Collections.emptyList());

        List<PostResponse> responses = new ArrayList<>();

        if (savedPosts != null) {
            for (Posts post : savedPosts) {

                List<String> roles = post.getUser().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                UserResponse userResponse = new UserResponse(post.getUser()
                        .getId(), post.getUser().getFullName(), post.getUser().getEmail(),post.getSemester(), roles);

                String fileUrl = null;
                String fileType = null;
                String fileName =null;

                if (post.getFilename() != null) {
                    fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(post.getFilename())
                            .toUriString();
                    fileType = post.getFileType();
                }

                responses.add(new PostResponse(post.getId(), post.getCaption(), post.getContent(),post.getSubject(),post.getSemester(), post.getDate(), userResponse, fileUrl, fileType,fileName));
            }
            return responses;
        } else {
            return responses;
        }
    }

    @Override
    public String savePost(Long post_id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new AppException("User not found"));

        if(post_id != null){


            if(!postRepository.checkPostAlreadySaved(user.getId(),post_id)) {
                Posts post = postRepository.findById(post_id).orElseThrow(() -> new AppException("Post not found"));

                user.getSavedPosts().add(post);
                userRepository.save(user);
                return "Post Saved";

            }else{
                return "Post Already Saved";
            }
        }else {
            return "Failed to save Post";
        }
    }

    @Override
    public String deleteSavedPost(Long post_id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new AppException("User not found"));

        if(post_id != null){
                Posts post = postRepository.findById(post_id).orElseThrow(() -> new AppException("Post not found"));

                user.getSavedPosts().remove(post);
                userRepository.save(user);


                //Again Fetch the saved post
                getUserSavedPost();
                return "Post Unsaved";
        }else {
            return "Failed to unsave Post";
        }
    }


}
