package com.bernhack.BCAConnect.controller.admin;


import com.bernhack.BCAConnect.constant.StringConstant;
import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/")
public class AdminController extends BaseController {


    @Autowired
    private AdminService adminService;

    @GetMapping("/getModerators")
    public ResponseEntity<GlobalAPIResponse> getModerators(){
        return successResponse(StringConstant.FETCH_MODERATOR,adminService.getAllModerators(RoleEnum.MODERATOR.name()));
    }

    @PostMapping("/makeModerator/{username}")
    public ResponseEntity<GlobalAPIResponse> makeModerators(@PathVariable String username){
        return successResponse(StringConstant.MODERATOR_MADE,adminService.makeModerator(username));
    }

    @PostMapping("/removeModerator/{username}")
    public ResponseEntity<GlobalAPIResponse> removeModerators(@PathVariable String username){
        return successResponse(StringConstant.MODERATOR_REMOVED,adminService.removeModerator(username));
    }


}
