package com.bernhack.BCAConnect.controller.moderator;


import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.dto.post.VerifyPostRequest;
import com.bernhack.BCAConnect.service.ModeratorService;
import com.bernhack.BCAConnect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moderator")
public class ModeratorController extends BaseController {

@Autowired
private ModeratorService moderatorService;



    @GetMapping("/getUsers")
    public ResponseEntity<GlobalAPIResponse> getAllUsers(){
        return successResponse(StringConstant.FETCH_ALL_USER,moderatorService.getAllUsers());
    }

    @GetMapping("/unverifiedPosts")
    public ResponseEntity<GlobalAPIResponse> getAllUnverifed(){
        return successResponse(StringConstant.UNVERIFIED_POST,moderatorService.getAllUnverifiedPosts());
    }

//    @DeleteMapping("/delete/{username}")
//    public ResponseEntity<GlobalAPIResponse> deleteUser(@PathVariable String username){
//        return successResponse(StringConstant.USER_DELETED,userService.deleteUser(username));
//    }


    @PostMapping("/verifyPost")
    public ResponseEntity<GlobalAPIResponse> verifyPost(@RequestBody VerifyPostRequest verifyPostRequest){
        return successResponse(StringConstant.POST_VERIFICATION,moderatorService.postVerification(verifyPostRequest));
    }

}
