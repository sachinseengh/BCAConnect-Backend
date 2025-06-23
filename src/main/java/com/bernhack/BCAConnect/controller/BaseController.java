package com.bernhack.BCAConnect.controller;

import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    public ResponseEntity<GlobalAPIResponse> successResponse(String message,Object data){
        GlobalAPIResponse response = new GlobalAPIResponse(HttpStatus.OK.value(),message,data);
        return ResponseEntity.ok(response);
    }
}
