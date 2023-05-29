package com.bilgeadam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordMailResponseDto { // değişti UserprofileChangePasswordRequestDto oldu 8 mayıs 2023
    private String password;
    private String email;
}
