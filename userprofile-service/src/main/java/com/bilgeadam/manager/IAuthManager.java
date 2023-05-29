package com.bilgeadam.manager;

import com.bilgeadam.dto.request.ChangePasswordRequestDto;
import com.bilgeadam.dto.request.UpdateEmailOrUsernameRequestDto;
import com.bilgeadam.repository.entity.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "auth-service", url = "http://localhost:8090/api/v1/auth")
public interface IAuthManager {
    @PutMapping("/update-username-email")
    public ResponseEntity<Boolean> updateMail(@RequestBody UpdateEmailOrUsernameRequestDto dto); // ismi updateUsernameOrEmail olarak değişebilir
    @GetMapping("/find-by-role/{role}")
    public ResponseEntity<List<Long>> findByRole(@PathVariable String role);
    @PutMapping("/update-password")
    public ResponseEntity<Boolean> updatePassword(@RequestBody ChangePasswordRequestDto dto);
}
