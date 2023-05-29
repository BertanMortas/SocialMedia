package com.bilgeadam.controller;

import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.bilgeadam.constant.ApiUrls.*;
@RestController
@RequestMapping(ELASTIC)
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    @GetMapping(FIND_ALL)
    public ResponseEntity<Iterable<UserProfile>> findAll(int currentPage, int size,String sortParameter,String sortDirection){
        return ResponseEntity.ok(userProfileService.findAll(currentPage,size, sortParameter, sortDirection));
    }
    @GetMapping(FIND_ALL+"2")
    public ResponseEntity<Iterable<UserProfile>> findAll(){
        return ResponseEntity.ok(userProfileService.findAll());
    }
}
