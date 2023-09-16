package com.example.jwtoauthlogin.domain.jwt.filter;

import com.example.jwtoauthlogin.domain.jwt.service.JwtService;
import com.example.jwtoauthlogin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않는 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급. RefreshToken 재발급(RTR 방식)
 *      인증 성공 처리는 하지 않고 실패 처리
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
            // "/login" 요청이 들어오면, 다음 필터를 호출하고 탈출.
        }

        /**
         * 사용자 요청 헤더에서 RefreshToken 추출
         * RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르면) null 반환
         * 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
         */
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        /*
        리프레시 토큰이 요청 헤더에 존재했다면, 사용자가 AccessToken이 만료되서
        RefreshToken까지 보낸 것이므로 리프레시 토큰이 db의 리프레시 토큰과 일치하는지 판단 후 일치하면 AccessToken을 재발급함
         */
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
            // RefreshToken을 보낸 경우에는 AccessToken을 재발급하고 인증 처리는 하지 않게 하기 위해 바로 return으로 필터 진행 막기
        }

        /**
         * RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
         * AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가므로 403에러 발생
         * AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가므로 인증 성공 처리
         */
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /**
     * 리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드
     * 파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 db에서 유저를 찾고, 해당 유저가 있는 경우에는
     * JwtService.createAccessToken()으로 AccessToken 생성.
     * reIssueRefreshToken()로 리프레시 토큰 재발급 & db에 리프레시 토큰 업데이터 메소드 호출.
     * 그 후 JwtService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
                            reIssuedRefreshToken);
                });
    }
}
