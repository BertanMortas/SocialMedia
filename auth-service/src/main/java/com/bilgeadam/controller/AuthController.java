package com.bilgeadam.controller;

import com.bilgeadam.dto.request.LoginRequestDto;
import com.bilgeadam.dto.request.ReqisterRequestDto;
import com.bilgeadam.dto.request.StatusRequestDto;
import com.bilgeadam.dto.request.UpdateEmailOrUsernameRequestDto;
import com.bilgeadam.dto.request.ChangePasswordRequestDto;
import com.bilgeadam.dto.response.LoginResponseDto;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.dto.response.StatusResponseDto;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.repository.entity.Enums.ERole;
import com.bilgeadam.service.AuthService;
import static com.bilgeadam.constant.ApiUrls.*;
import com.bilgeadam.utility.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping(REGISTER)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid ReqisterRequestDto dto){
        RegisterResponseDto responseDto = authService.register(dto);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping(REGISTER+"2")// rabbit mq
    public ResponseEntity<RegisterResponseDto> register2(@RequestBody @Valid ReqisterRequestDto dto){
        RegisterResponseDto responseDto = authService.registerRabbitmq(dto);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping(LOGIN)
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto){
        String token = authService.login(dto);
        if (!token.isEmpty()) {
            return ResponseEntity.ok(LoginResponseDto.builder().username(dto.getUsername()).password(dto.getPassword()).activationCode(token).build());
        }
        throw new NotFoundException("kullanıcı bulunamadı");
    }
    @PostMapping(ACTIVATE_STATUS)
    public ResponseEntity<StatusResponseDto> activation(@RequestBody StatusRequestDto dto){
        authService.activateStatus(dto);
        return ResponseEntity.ok().build();
    }
    @GetMapping(FIND_ALL)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<Auth>> findAll(){
        return ResponseEntity.ok(authService.findAll());
    }
    @PutMapping("/update-username-email")
    public ResponseEntity<Boolean> update(@RequestBody UpdateEmailOrUsernameRequestDto dto){
        return ResponseEntity.ok(authService.update(dto));
    }
    @GetMapping("/create-token-with-id")
    public ResponseEntity<String> createToken(Long id){
        return ResponseEntity.ok(jwtTokenProvider.createToken(id).get());
    }
    @GetMapping("/create-token-with-role")
    public ResponseEntity<String> createToken(Long id, ERole role){
        return ResponseEntity.ok(jwtTokenProvider.createToken(id,role).get());
    }
    @GetMapping("/get-id-from-token")
    public ResponseEntity<Long> getIdFromToken(String token){
        return ResponseEntity.ok(jwtTokenProvider.getIdFromToken(token).get());
    }
    @GetMapping("/get-role-from-token")
    public ResponseEntity<String> getRoleFromToken(String token){
        return ResponseEntity.ok(jwtTokenProvider.getRoleFromToken(token).get());
    }
    @GetMapping("/delete")
    public ResponseEntity<Boolean> delete(String token){
        return ResponseEntity.ok(authService.delete(token));
    }
    @GetMapping("/find-by-role/{role}")
    public ResponseEntity<List<Long>> findByRole(@PathVariable String role){
        return ResponseEntity.ok(authService.findByRole(role));
    }
    @PutMapping("/update-password")
    public ResponseEntity<Boolean> updatePassword(@RequestBody ChangePasswordRequestDto dto){
        return ResponseEntity.ok(authService.updatePassword(dto));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPassword(String mail, String username){
        return ResponseEntity.ok(authService.forgotPassword(mail,username));
    }
/*    @GetMapping("/redis")
    @Cacheable(value = "redisexample")
    public String redisExample(String value){
        try{
            Thread.sleep(2000);
            return value;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
    @GetMapping("cache-delete")
    @CacheEvict(cacheNames = "redisexample",allEntries = true)
    public void cacheDelete(){
        System.out.println("caches are deleted");
    }*/
}
