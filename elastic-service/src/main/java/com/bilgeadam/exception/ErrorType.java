package com.bilgeadam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_ERROR(5100, "Sunucu Hatası", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(4000, "Parametre Hatası", HttpStatus.BAD_REQUEST),
    USERNAME_DUPLICATE(4300, "Bu kullanıcı zaten kayıtlı", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(4400, "Böyle bir kullanıcı bulunamadı", HttpStatus.NOT_FOUND),
    USER_NAME_NOT_CREATED(4100, "Böyle bir kullanıcı oluşturulamadı", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(4404, "Böyle bir token bulunamadı", HttpStatus.NOT_FOUND),
    ACTIVATE_CODE_ERROR(4500, "Aktivasyon kod hatası", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    HttpStatus httpStatus;
}
