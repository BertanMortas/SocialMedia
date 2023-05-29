package com.bilgeadam.config.security;

import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {
    private final AuthService authService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    public UserDetails loadUserByUsername(Long id) throws UsernameNotFoundException {
        Optional<Auth> auth = authService.findById(id);
        if (auth.isPresent()) {
            /**
             *Rolleri normalde liste şeklinde tutarız ama bu uygulamada tek bir rolü varmış gibi tuttuk.
             * Eğer liste olarak tutarsak ==>
             * List<GrantedAuthority> authorityList = new ArrayList<>();
             * Auth authRole = authService.findById(id);
             * authRole.getRoles().forech(roles ->
             * {authorityList.add(new SimpleGrantedAuthority(roles));})
             */
            //GrantedAuthority --> Bir yetkilendirme mekanizmasını ve kullanıcının rollerini temsil eder.
            //SimpleGrantedAuthority --> Kullanıcının tek bir rolü olduğunu varsayar
            GrantedAuthority grantedAuthority =new SimpleGrantedAuthority(auth.get().getRole().toString());
            return User.builder()
                    .username(auth.get().getUsername())
                    .password("")// şifre doğrulama değil role üzerinden yaptığımız için boş
                    .accountExpired(false) //kullanıcnın hesabının süresinin dolmadığını belirtir
                    .accountLocked(false)
                    .authorities(grantedAuthority) //kullanıcının her bir işlem için buradaki rol bilgisi karşılaştırılır
                    .build();
        }
        return null;
    }
}
