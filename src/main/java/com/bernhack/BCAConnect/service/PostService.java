package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.post.UpdatePostRequest;
import com.bernhack.BCAConnect.dto.post.CreatPostRequest;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

public interface PostService {


    String createPost(MultipartFile file,CreatPostRequest creatPostRequest) throws IOException;

    String updatePost(UpdatePostRequest updatePostRequest);

    String deletePost(Long id);


    String postVerification(VerifyPostRequest verifyPostRequest);
}
