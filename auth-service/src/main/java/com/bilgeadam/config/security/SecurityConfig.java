package com.bilgeadam.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //hasRole() metodunun çalışması için için kullanılır
@EnableGlobalMethodSecurity(prePostEnabled = true) // metodları işaretleyerek role kontrolü yapmaya yarar
public class SecurityConfig {
    @Bean
    JwtFilter getJwtFilter(){
        return new JwtFilter();
    }
    //SecurityFilterChain --> gelen istekleri işlemek ve filtreyelerek bir zincir oluşturmayı sağlar
    //HttpSecurity --> Http işlemlerinde ki güvenlikten sorumludur. Kimlik doğrulama veya session işlemlerinde
    // hangi kişinin endpointlere erişip erişemeyeceğini bilirler. Rollere göre kontrol yapar
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // cors --> http'nin oluşturabileceği güvenlik açıklarını gidermek için browserların kullandığı
                // bir erişim kısıtlama prensibidir
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api/v1/auth/login",
                        "/api/v1/auth/loginMd5",
                        "/api/v1/auth/register",
                        "/api/v1/auth/registermd5",
                        "/api/v1/auth/activate-status",
                        "/api/v1/auth/forgot-password").permitAll().anyRequest().authenticated(); //find-all
        //JwtFilterdan gelen bilgilere göre burada endpointler için son olarak bir filtreleme daha yapılır.
        httpSecurity.addFilterBefore(getJwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
