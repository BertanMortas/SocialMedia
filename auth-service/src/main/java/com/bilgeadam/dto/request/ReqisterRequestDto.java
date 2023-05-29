package com.bilgeadam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqisterRequestDto {
    @NotBlank(message = "kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 20,message = "kullanıcı adı en az 3 en fazla 20 olabilir")
    private String username;
    @Email(message = "lütfen geçerli bir mail adresi giriniz")
    private String email;
    @NotBlank
    @Size(min = 8, max = 32,message = "kullanıcı şifre en az 8 en fazla 32 olabilir")
    private String password;
    private String repassword;
}
