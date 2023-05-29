package com.bilgeadam.controller;

import com.bilgeadam.dto.request.ChangePasswordRequestDto;
import com.bilgeadam.dto.request.NewCreateUserRegisterDto;
import com.bilgeadam.dto.request.UserProfileUpdateRequestDto;
import com.bilgeadam.dto.request.UserprofileChangePasswordRequestDto;
import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.bilgeadam.constant.ApiUrls.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;
    @PostMapping(CREATE)
    public ResponseEntity<Boolean> createUser(@RequestBody NewCreateUserRegisterDto dto){
        return ResponseEntity.ok(userProfileService.createUser(dto));
    }
    @GetMapping(FIND_ALL)
    public ResponseEntity<List<UserProfile>> findAll(){
        return ResponseEntity.ok(userProfileService.findAll());
    }
    @GetMapping("/activate-status/{authId}")
    public ResponseEntity<Boolean> activateStatus(@PathVariable Long authId){
        return ResponseEntity.ok(userProfileService.activateStatus(authId));
    }
    @PutMapping(UPDATE)
    public ResponseEntity<UserProfile> update(@RequestBody UserProfileUpdateRequestDto dto){
        return ResponseEntity.ok(userProfileService.update(dto));
    }
    @DeleteMapping(DELETE_BY_ID+"/{authId}")
    public ResponseEntity<Boolean> delete(@PathVariable long authId){
        return ResponseEntity.ok(userProfileService.delete(authId));
    }
    @GetMapping("/find-by-username")
    public ResponseEntity<UserProfile> findByUsername(String username){
        return ResponseEntity.ok(userProfileService.findByUsername(username));
    }
    @GetMapping("/find-by-role/{role}")
    public ResponseEntity<List<UserProfile>> findByRole(@PathVariable String role){
        return ResponseEntity.ok(userProfileService.findByRole(role));
    }
    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordRequestDto dto){
        return ResponseEntity.ok(userProfileService.passwordChange(dto));
    }
    @PutMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPasswordUser(@RequestBody UserprofileChangePasswordRequestDto dto){
        return ResponseEntity.ok(userProfileService.forgotPasswordUser(dto));
    }
    @GetMapping("/find-by-auth-id/{authId}")
    public ResponseEntity<Optional<UserProfile>> findByAuthId(@PathVariable Long authId){
        return ResponseEntity.ok(userProfileService.findOptionalByAuthId(authId));
    }
}
