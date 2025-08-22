package com.bernhack.BCAConnect.controller.post;

import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.dto.post.CreatPostRequest;
import com.bernhack.BCAConnect.dto.post.UpdatePostRequest;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
import com.bernhack.BCAConnect.service.PostService;
import com.bernhack.BCAConnect.service.UserService;
import com.fasterxml.jackson.databind.ser.Serializers;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/post/")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<GlobalAPIResponse> createPost(@RequestPart(value = "file", required = false) MultipartFile file, @ModelAttribute CreatPostRequest creatPostRequest) throws IOException{

        return successResponse(StringConstant.POST_CREATED,postService.createPost(file,creatPostRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<GlobalAPIResponse> updatePost(@RequestBody UpdatePostRequest updatePostRequest){
        return successResponse(StringConstant.POST_UPDATED,postService.updatePost(updatePostRequest));
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity<GlobalAPIResponse> getAllPosts(){
        return successResponse(StringConstant.POST_FETCHED,postService.getAllPosts());
    }

    @DeleteMapping("/delete/{post_id}")
    public ResponseEntity<GlobalAPIResponse> deletePost(@PathVariable Long post_id){
        return successResponse(StringConstant.POST_DELETED,postService.deletePost(post_id));
    }

    @PostMapping("/getUserPost/{username}")
    public ResponseEntity<GlobalAPIResponse> getUserPost(@PathVariable String username){
        return successResponse(StringConstant.USER_POSTS,postService.getUserPost(username));
    }


    @PostMapping("/savePost/{post_id}")
    public ResponseEntity<GlobalAPIResponse> saveUserPost(@PathVariable Long post_id){
        return successResponse(StringConstant.POST_SAVED,postService.savePost(post_id));
    }

    @GetMapping("/savedPosts")
    public ResponseEntity<GlobalAPIResponse> getSavedPost(){
        return successResponse(StringConstant.SAVED_POSTS,postService.getUserSavedPost());
    }

    @PostMapping("/unsavePost/{post_id}")
    public ResponseEntity<GlobalAPIResponse> unsaveUserPost(@PathVariable Long post_id){
        return successResponse(StringConstant.POST_UNSAVED,postService.deleteSavedPost(post_id));
    }



}
