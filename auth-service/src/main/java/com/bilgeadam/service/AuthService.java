package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.exception.AuthManagerException;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.manager.IEmailManager;
import com.bilgeadam.manager.IUserProfileManager;
import com.bilgeadam.mapper.IAuthMapper;
import com.bilgeadam.rabbitmq.model.RegisterModel;
import com.bilgeadam.rabbitmq.producer.RegisterMailProducer;
import com.bilgeadam.rabbitmq.producer.RegisterProducer;
import com.bilgeadam.repository.IAuthRepository;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.repository.entity.Enums.ERole;
import com.bilgeadam.repository.entity.Enums.EStatus;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.bilgeadam.utility.CodeGenerator.generateCode;

@Service
public class AuthService extends ServiceManager<Auth,Long> {
    private final IAuthRepository authRepository;
    private final IUserProfileManager iUserProfileManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegisterProducer registerProducer;
    private final RegisterMailProducer registerMailProducer;
    private final PasswordEncoder passwordEncoder;
    private final IEmailManager emailManager;

    public AuthService(IAuthRepository authRepository, RegisterProducer registerProducer, IUserProfileManager iUserProfileManager, JwtTokenProvider jwtTokenProvider, RegisterMailProducer registerMailProducer, PasswordEncoder passwordEncoder, IEmailManager emailManager){
        super(authRepository);
        this.authRepository=authRepository;
        this.iUserProfileManager=iUserProfileManager;
        this.jwtTokenProvider=jwtTokenProvider;
        this.registerProducer=registerProducer;
        this.registerMailProducer = registerMailProducer;
        this.passwordEncoder = passwordEncoder;
        this.emailManager = emailManager;
    }
    public Optional<Auth> findById(Long id){
        return authRepository.findById(id);
    }
    public List<Auth> findAll(){
        return authRepository.findAll();
    }
    public RegisterResponseDto register(ReqisterRequestDto dto){
        Auth auth = save(IAuthMapper.INSTANCE.toAuth(dto));
        if (dto.getPassword().equals(dto.getRepassword())) {
            auth.setActivationCode(generateCode());
            auth.setPassword(passwordEncoder.encode(dto.getPassword()));
          //  auth.setActivationCode(jwtTokenProvider.createToken(auth.getId()).get());
            iUserProfileManager.createUser(IAuthMapper.INSTANCE.fromAuthToNewCreateUserDto(auth)); // user a gönderdiğimiz kısım burası
            save(auth);
        }else{
            throw new AuthManagerException(ErrorType.PASSWORD_ERROR);
        }
        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.toDtoResponse(auth);
        return responseDto;
    }
    public String login(LoginRequestDto dto){
        Optional<Auth> auth = authRepository.findByUsername(dto.getUsername());
        String token =
                jwtTokenProvider.createToken(auth.get().getId(),auth.get().getRole())
                        .orElseThrow(()->{throw new AuthManagerException(ErrorType.TOKEN_NOT_FOUND);
                        });
        if (auth.isEmpty() || !passwordEncoder.matches(dto.getPassword(), auth.get().getPassword())) {
            throw new AuthManagerException(ErrorType.LOGIN_ERROR);
        } else if (!auth.get().getStatus().equals(EStatus.ACTIVE)) {
            throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
        }
        return token;
    }
    public boolean activateStatus(StatusRequestDto dto){
        Optional<Auth> auth =findById(dto.getId());
        if (auth.isEmpty()) {
           throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        } else if (dto.getActivationCode().equals(auth.get().getActivationCode())) {
            auth.get().setStatus(EStatus.ACTIVE);
            iUserProfileManager.activateStatus(auth.get().getId()); // user a gönderdim
            update(auth.get());
            return true;
        }
        throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
    }
    public boolean update(UpdateEmailOrUsernameRequestDto dto){
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        /*
        userprofile da yapılan çözüm ile devam ederkenki çözüm
        auth.get().setEmail(dto.getEmail());
        auth.get().setUsername(dto.getUsername());*/
        IAuthMapper.INSTANCE.updateUsernameOrEmail(dto,auth.get());
        update(auth.get());
        return true;
    }
    public Boolean delete(String token){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(token);
        if (authId.isEmpty()) {
            throw new AuthManagerException(ErrorType.TOKEN_NOT_FOUND);
        }
        Optional<Auth> auth = authRepository.findById(authId.get());
        if (auth.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());
        return true;
    }
    public RegisterResponseDto registerRabbitmq(ReqisterRequestDto dto){
        Auth auth = save(IAuthMapper.INSTANCE.toAuth(dto));
        if (dto.getPassword().equals(dto.getRepassword())) {
            auth.setActivationCode(generateCode());
            auth.setPassword(passwordEncoder.encode(dto.getPassword()));

            RegisterModel registerModel = IAuthMapper.INSTANCE.fromAuthToRegisterModel(auth);
            registerProducer.sendNewUser(registerModel);
            registerMailProducer.sendActivationCode(IAuthMapper.INSTANCE.fromAuthToRegisterMailModel(auth));
            save(auth);
        }else{
            throw new AuthManagerException(ErrorType.PASSWORD_ERROR);
        }
        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.toDtoResponse(auth);
        return responseDto;
    }
    public Boolean forgotPassword(String email, String username){
        Optional<Auth> auth = authRepository.findOptionalByEmail(email);
        if (auth.get().getStatus().equals(EStatus.ACTIVE)){
            if (auth.get().getUsername().equals(username)){
                //random password variable
                String randomPassword = UUID.randomUUID().toString();
                auth.get().setPassword(passwordEncoder.encode(randomPassword));
                save(auth.get());
                System.out.println(randomPassword);
                ForgotPasswordMailResponseDto dto = ForgotPasswordMailResponseDto.builder()
                        .password(randomPassword)
                        .email(email)
                        .build();
                emailManager.forgotPasswordMail(dto);
                UserprofileChangePasswordRequestDto changePasswordRequestDto = UserprofileChangePasswordRequestDto.builder()
                        .authId(auth.get().getId()).password(auth.get().getPassword()).build();
                iUserProfileManager.forgotPasswordUser(changePasswordRequestDto);
                return true;
            }else {
                throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
            }
        }else {
            if (auth.get().getStatus().equals(EStatus.DELETED)){
                throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
            }
            throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
        }
    }
// localin sebabi admin -> ADMIN olması için büyük İ olmaması için
    public List<Long> findByRole(String role) {
       //ERole.valueOf(role.toUpperCase(Locale.ENGLISH));
        return authRepository.findByRole(ERole.valueOf(role.toUpperCase(Locale.ENGLISH))).stream().map(x ->x.getId()).collect(Collectors.toList());
    }

    public Boolean updatePassword(ChangePasswordRequestDto dto) {
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new AuthManagerException(ErrorType.TOKEN_NOT_FOUND);
        }
        Optional<Auth> auth = authRepository.findById(authId.get());
        if (auth.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setPassword(passwordEncoder.encode(dto.getNewPassword()));
        save(auth.get());
        // user a göndermeyi unutma
        return true;
    }
}
