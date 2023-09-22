package com.example.jwttest.jwt.configuration;

import com.example.jwttest.jwt.security.JwtAccessDeniedHandler;
import com.example.jwttest.jwt.security.JwtAuthenticationEntryPoint;
import com.example.jwttest.jwt.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Bean
    public BCryptPasswordEncoder encoder() {
        // 비밀번호를 DB에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
        return (web)
                -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 리소스들
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 인터셉터로 요청을 안전하게 보호하는 방법 설정
        http
                // jwt 토큰 사용을 위한 설정
                /**
                 * REST API는 상태 비저장이고 인증 토큰에 의존하기 때문에 CSRF 보호가 필요하지 않습니다.
                 * CSRF 공격은 일반적으로 사용자 브라우저를 속여 사용자가 모르는 사이에 요청을 하도록 합니다.
                 * REST API의 경우 클라이언트의 인증 정보가 안전하게 저장되고 각 요청과 함께 전송되므로 공격자가 쉽게 조작할 수 없습니다.
                 */
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                // AuthenticationFilter가 요청을 가로채고, 가로챈 정보를 통해 UsernamePasswordAuthenticationToken의 인증용 객체를 생성한다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//        코드 조각에서와 같이 세션 생성 정책을 STATELESS로 설정하면 Spring Security 구성이 상태 비저장 방식으로 작동하도록 설계되었음을 의미합니다. 이 모드에서는:
//
//        Spring Security는 인증된 사용자에 대해 HTTP 세션을 생성하지 않습니다.
//                각 수신 요청에는 인증 정보(귀하의 경우 일반적으로 JWT 토큰 형식)가 포함되어 있어야 합니다.
//        서버는 사용자 관련 세션 데이터를 서버 측에 저장하지 않습니다. 이를 통해 애플리케이션은 확장 가능하고 각 요청에 인증에 필요한 모든 정보가 포함되어야 하는 RESTful API 및 마이크로서비스에 적합합니다.
                /**
                 * STATELESS로 세션 정책을 설정한다는 것은, 세션쿠키 방식의 인증 메커니즘 방식을 사용하지 않겠다는 것을 의미한다. 인증에 성공한 이후라도 클라이언트가 다시 어떤 자원에 접근을 시도할 경우, SecurityContextPersistenceFilter는 세션 존재 여부를 무시하고 항상 새로운 SecurityContext 객체를 생성하기 때문에 인증성공 당시 SecurityContext에 저장했던 Authentication 객체를 더 이상 참조 할 수 없게 된다.
                 */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 예외 처리
                .and()
                .exceptionHandling()
                //승인되지 않은 사용자가 보호된 리소스에 액세스하려고 할 때 리디렉션되거나 오류 응답이 제공되는 지점입니다.
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) //customEntryPoint
                .accessDeniedHandler(jwtAccessDeniedHandler) // cutomAccessDeniedHandler

                .and()
                .authorizeRequests() // '인증'이 필요하다
                // 아래의 URL에 대한 요청에는 인증이 필요합니다. 즉, 사용자가 이러한 리소스에 액세스하려면 로그인해야 합니다.
                .antMatchers("/api/mypage/**").authenticated() // 마이페이지 인증 필요
                //  "/api/admin/" 아래의 URL에 대한 요청은 "ADMIN" 역할을 가진 사용자로 제한됩니다. 이 역할을 가진 사용자만 이러한 리소스에 액세스할 수 있습니다.
                .antMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 페이지
                .anyRequest().permitAll()

                .and()
                /**
                 * .headers(): 이 섹션은 HTTP 응답에 대한 보안 헤더를 구성하는 데 사용됩니다.
                 * .frameOptions().sameOrigin(): "X-Frame-Options" 헤더를 "SAMEORIGIN"으로 구성합니다.
                 * 이는 페이지가 동일한 출처의 페이지로만 프레이밍되도록 허용하여 클릭재킹 공격으로부터 보호하는 데 도움이 됩니다.
                 *
                 */
                .headers()
                .frameOptions().sameOrigin();

        return http.build();
    }

    /**
     * 이 코드의 주요 역할은 Spring Security의 AuthenticationManager 빈을 빌드하고 구성된 인증 프로세스를 활용할 수 있도록 합니다.
     * AuthenticationManager는 사용자의 자격 증명을 검증하고 인증된 사용자 객체를 반환하는 중요한 역할을 수행합니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
