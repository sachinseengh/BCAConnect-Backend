package com.bernhack.BCAConnect.healthCheck;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {


    @GetMapping("/")
    public String checkHealth(){
        return "Your Backend is running";
    }
}
