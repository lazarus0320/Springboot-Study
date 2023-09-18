package com.example.jwtoauthlogin.domain.global.config;

import com.example.jwtoauthlogin.domain.global.jwt.service.JwtService;
import com.example.jwtoauthlogin.domain.global.jwt.service.LoginService;
import com.example.jwtoauthlogin.domain.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.example.jwtoauthlogin.domain.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.example.jwtoauthlogin.domain.global.oauth2.service.CustomOAuth2UserService;
import com.example.jwtoauthlogin.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .formLogin().disable()
//                .httpBasic().disable()
//                .csrf().disable()
//                .headers().frameOptions().disable()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                //== URL별 권한 관리 옵션 ==//
//                .authorizeHttpRequests()
//                // 아이콘, css, js 관련
//                // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능, h2-console에 접근 가능
//                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
//                .antMatchers("/sign-up").permitAll()
//                .anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
//    }
}
