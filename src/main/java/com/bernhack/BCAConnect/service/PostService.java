package com.bernhack.BCAConnect.service;

import com.bernhack.BCAConnect.dto.post.PostResponse;
import com.bernhack.BCAConnect.dto.post.UpdatePostRequest;
import com.bernhack.BCAConnect.dto.post.CreatPostRequest;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

public interface PostService {


    String createPost(MultipartFile file,CreatPostRequest creatPostRequest) throws IOException;

    String updatePost(UpdatePostRequest updatePostRequest);

    String deletePost(Long id);

    List<PostResponse> getAllPosts();

    List<PostResponse> getUserPost();


    List<PostResponse> getUserSavedPost();


    String savePost(Long post_id);

    String deleteSavedPost(Long post_id);
}
