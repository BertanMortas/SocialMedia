package com.bilgeadam.manager;

import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-mail", url = "http://localhost:8085/api/v1/mail")
public interface IEmailManager {
    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPasswordMail(@RequestBody ForgotPasswordMailResponseDto dto);
}
