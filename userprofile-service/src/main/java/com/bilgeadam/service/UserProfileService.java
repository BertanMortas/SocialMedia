package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserProfileManagerException;
import com.bilgeadam.manager.IAuthManager;
import com.bilgeadam.mapper.IUserProfileMapper;
import com.bilgeadam.rabbitmq.model.RegisterModel;
import com.bilgeadam.rabbitmq.producer.RegisterElasticProducer;
import com.bilgeadam.repository.IUserProfileRepository;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.repository.enums.EStatus;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService extends ServiceManager<UserProfile,String> {
    private final IUserProfileRepository userProfileRepository;
    private final IAuthManager iAuthManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheManager cacheManager;
    private final RegisterElasticProducer registerElasticProducer;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(IUserProfileRepository userProfileRepository, CacheManager cacheManager, IAuthManager iAuthManager, JwtTokenProvider jwtTokenProvider, RegisterElasticProducer registerElasticProducer, PasswordEncoder passwordEncoder){
        super(userProfileRepository);
        this.userProfileRepository=userProfileRepository;
        this.iAuthManager=iAuthManager;
        this.jwtTokenProvider=jwtTokenProvider;
        this.cacheManager=cacheManager;
        this.registerElasticProducer = registerElasticProducer;
        this.passwordEncoder = passwordEncoder;
    }
    @CacheEvict(value = "findByRole",allEntries = true)
    public Boolean createUser(NewCreateUserRegisterDto dto){
        try{
            save(IUserProfileMapper.INSTANCE.fromDtoToUserNewRequest(dto));
            cacheManager.getCache("findAll").clear();
            return true;
        } catch (Exception e){
            throw new RuntimeException("beklenmeyen bir hata oluştu");
        }
    }
    @Caching(evict = {
            @CacheEvict(value = "findAll", allEntries = true)
    })
    public Boolean createUserWithRabbitMq(RegisterModel model){
        try{
            UserProfile userProfile = save(IUserProfileMapper.INSTANCE.fromModelToUserNewRequest(model));
            registerElasticProducer.sendNewUser(IUserProfileMapper.INSTANCE.fromUserToModelNewRequest(userProfile)); // alstic e rabbitle gönderdik authdan geldi
            return true;
        } catch (Exception e){
            throw new RuntimeException("beklenmeyen bir hata oluştu");
        }
    }
    public Boolean activateStatus(Long authId){
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(authId);
        if (userProfile.isEmpty()) {
            throw new RuntimeException("kullanıcı bulunamadı");
        }
        userProfile.get().setStatus(EStatus.ACTIVE);
        update(userProfile.get());
        return true;
    }
    //Cache delete olmadan update oldu
    //@CachePut(value = "findByUsername", key = "#dto.username.toLowerCase()")//oluşan değişikler sonucunda cache in update edilmesini sağlar // unless koşullara göre cache ler
    public UserProfile update(UserProfileUpdateRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()){
            throw new UserProfileManagerException(ErrorType.TOKEN_NOT_FOUND);
        }
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        // cache de sildiğimiz için burda da küçüğe çevirmemiz lazım yoksa burada cache silinmez
        //cacheManager.getCache("findByUsername").evict(userProfile.get().getUsername().toLowerCase()); // redis cache silme
        cacheManager.getCache("findAll").clear();
        /* userprofile çözümü
        AuthService de değişklikler var onları da aç!!!
        burda userprofile çekiliyor sonra auth a güncellenen userdan gelen dta maplenip sonra autha gönderiyor

        update(IUserProfileMapper.INSTANCE.updateUserFromDto(dto,userProfile.get()));
        iAuthManager.updateMail(IUserProfileMapper.INSTANCE.toUpdateUsernameEmail(userProfile.get()));
        */
        UpdateEmailOrUsernameRequestDto updateEmailOrUsernameRequestDto = IUserProfileMapper.INSTANCE.toUpdateUsernameEmail(dto);
        updateEmailOrUsernameRequestDto.setAuthId(authId.get());
        iAuthManager.updateMail(updateEmailOrUsernameRequestDto);
        update(IUserProfileMapper.INSTANCE.updateUserFromDto(dto,userProfile.get()));
        //alttaki benim çözüm anotasyonlu arda hocanın
        cacheManager.getCache("findByUsername").put(userProfile.get().getUsername().toLowerCase(),userProfile.get()); // redis cache ekleme
        return userProfile.get();
    }
    public Boolean delete(Long authId){
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(authId);
        if (userProfile.isEmpty()){
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.INACTIVE);
        update(userProfile.get());
        return true;
    }
    @Cacheable(value = "findByUsername",key = "#username.toLowerCase()")
    public UserProfile findByUsername(String username){
        try {
            Thread.sleep(2000);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        Optional<UserProfile> userProfile = userProfileRepository.findByUsernameIgnoreCase(username);
        if (userProfile.isEmpty()) {
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        return userProfile.get();
    }
    @Cacheable(value = "findAll") // evict kısmını sor
    public List<UserProfile> findAll(){
        try {
            Thread.sleep(2000);
        }catch (Exception e){
            e.getMessage();
        }
        return userProfileRepository.findAll();
    }
    @Cacheable(value = "findByRole",key = "#role.toUpperCase()")
    public List<UserProfile> findByRole(String role){
        try {
            Thread.sleep(2000);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        // auth manager
        List<Long> authIds = iAuthManager.findByRole(role).getBody(); // responceEntity den kurtarmak için getBody yazdık
        return authIds.stream().map(
                x -> userProfileRepository.findOptionalByAuthId(x)
                        .orElseThrow(()->{throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);}))
                .collect(Collectors.toList());
    }

    /**
     * for followService
     * @param authId
     */
    public Optional<UserProfile> findOptionalByAuthId(Long authId) {
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(authId);
        if (userProfile.isEmpty()) {
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        return userProfile;
    }
    public Boolean passwordChange(ChangePasswordRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken()); // authid
        if (authId.isEmpty()){
            throw new UserProfileManagerException(ErrorType.TOKEN_NOT_FOUND);
        }
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), userProfile.get().getPassword())){
            throw new UserProfileManagerException(ErrorType.PASSWORD_ERROR);
        }
        userProfile.get().setPassword(passwordEncoder.encode(dto.getNewPassword()));
        cacheManager.getCache("findAll").clear();
        cacheManager.getCache("findByUsername").clear();
        iAuthManager.updatePassword(dto); // verileri gönderdiğimiz kısım feignClient
        userProfileRepository.save(userProfile.get());
        return true;
    }

    public Boolean forgotPasswordUser(UserprofileChangePasswordRequestDto dto) { // denemedim bi dene
        Optional<UserProfile> userProfile = userProfileRepository.findOptionalByAuthId(dto.getAuthId());
        if (userProfile.isEmpty()) {
            throw new UserProfileManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setPassword(dto.getPassword());
        update(userProfile.get());
        cacheManager.getCache("findAll").clear();
        cacheManager.getCache("findByUsername").clear();
        return true;
    }
}
