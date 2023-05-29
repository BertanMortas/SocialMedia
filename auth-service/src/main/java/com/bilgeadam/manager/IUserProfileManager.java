package com.bilgeadam.manager;

import com.bilgeadam.dto.request.NewCreateUserRegisterDto;
import com.bilgeadam.dto.request.UserprofileChangePasswordRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.bilgeadam.constant.ApiUrls.*;

@FeignClient(name = "user-profile-service", url = "http://localhost:8080/api/v1/user-profile")
public interface IUserProfileManager {
    @PostMapping("/create")
    public ResponseEntity<Boolean> createUser(@RequestBody NewCreateUserRegisterDto dto);
    @GetMapping("/activate-status/{authId}")
    public ResponseEntity<Boolean> activateStatus(@PathVariable Long authId);
    @DeleteMapping(DELETE_BY_ID+"/{authId}")
    public ResponseEntity<Boolean> delete(@PathVariable long authId);
    @PutMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPasswordUser(@RequestBody UserprofileChangePasswordRequestDto dto);
}
