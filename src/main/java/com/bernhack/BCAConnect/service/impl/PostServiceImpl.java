package com.bernhack.BCAConnect.service.impl;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.dto.post.UpdatePostRequest;
import com.bernhack.BCAConnect.dto.post.CreatPostRequest;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("User not found"));

        Posts post = new Posts();

        post.setSubject(creatPostRequest.getSubject());
        post.setCaption(creatPostRequest.getCaption());
        post.setSemester(creatPostRequest.getSemester());
        post.setContent(creatPostRequest.getContent());
        post.setIsVerifed(false);
        post.setDate(LocalDateTime.now());

        post.setUser(user);

        String fileName=null;
        String fileType=null;
        if (file != null && !file.isEmpty()) {
            fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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

        User user = userRepository.findByUserName(username).orElseThrow(()->new AppException("User not found"));


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
        post.setIsVerifed(false);
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
    public String postVerification(VerifyPostRequest verifyPostRequest) {

        Posts post = postRepository.findById(verifyPostRequest.getId()).orElseThrow(()->new AppException("Post Not Found"));

        if(verifyPostRequest.isVerifed()){

            User user = post.getUser();
            post.setIsVerifed(true);
            Notes note = new Notes();
            note.setSemester(post.getSemester());
            note.setSubject(post.getSubject());
            note.setFileName(post.getFilename());
            note.setDate(LocalDateTime.now());
            note.setUser(user);
            noteRepository.save(note);

            return StringConstant.POST_VERIFIED;
        }else{
            postRepository.delete(post);
            return StringConstant.POST_DECLINED;
        }
    }


}
