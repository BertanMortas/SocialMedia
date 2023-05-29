package com.bilgeadam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/auth-service")
    public ResponseEntity<String> authServiceFallback(){
        return ResponseEntity.ok("auth service şu anda hizmet veremiyor");
    }
    @GetMapping("/user-profile-service")
    public ResponseEntity<String> userProfileServiceFallback(){
        return ResponseEntity.ok("userProfile service şu anda hizmet veremiyor");
    }
    @GetMapping("/post-service")
    public ResponseEntity<String> postServiceFallback(){
        return ResponseEntity.ok("post service şu anda hizmet veremiyor");
    }
    @GetMapping("/mail-service")
    public ResponseEntity<String> mailServiceFallback(){
        return ResponseEntity.ok("mail service şu anda hizmet veremiyor");
    }
}
