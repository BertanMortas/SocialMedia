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
    POST_NOT_FOUND(4400, "Böyle bir post bulunamadı", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(4400, "Böyle bir post bulunamadı", HttpStatus.NOT_FOUND),
    USER_NAME_NOT_CREATED(4100, "Böyle bir kullanıcı oluşturulamadı", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(4404, "Böyle bir token bulunamadı", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(4405, "Böyle bir token geçerli değildir", HttpStatus.NOT_FOUND),
    ACTIVATE_CODE_ERROR(4500, "Aktivasyon kod hatası", HttpStatus.BAD_REQUEST),
    FOLLOW_ALREADY_EXIST(4450, "takip isteği daha önce oluşturulmuş", HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR(4900, "şifreler aynı değil değiştirmek için aynı şifreleri giriniz", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    HttpStatus httpStatus;
}
