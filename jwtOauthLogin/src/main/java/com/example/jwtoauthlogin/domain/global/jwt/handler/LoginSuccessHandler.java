package com.example.jwtoauthlogin.domain.global.jwt.handler;

import com.example.jwtoauthlogin.domain.global.jwt.service.JwtService;
import com.example.jwtoauthlogin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    /**
     *
     * JSON 로그인 필터를 정상적으로 통과해서 인증처리가 됐기 때문에,
     *
     * 내부에서 AccessToken과 RefreshToken을 생성해서 Response에 보내줍니다.
     *
     *
     *
     * 유저 회원가입 시에는 RefreshToken이 없기 때문에 RefreshToken Column이 null입니다.
     *
     * 따라서, 로그인 성공 시 RefeshToken을 발급하고, DB에 저장해야합니다.
     *
     * DB에 발급된 RefreshToken을 저장하기 위해 유저 테이블의 RefreshToken Column에 업데이트하는
     *
     * updateRefreshToken()으로 업데이트 후 saveAndFlush()로 DB에 반영합니다.
     */
    @Override
    public void onAuthenticationSuceess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
