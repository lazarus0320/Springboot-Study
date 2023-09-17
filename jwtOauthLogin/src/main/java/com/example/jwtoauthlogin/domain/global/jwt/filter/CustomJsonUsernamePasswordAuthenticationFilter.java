package com.example.jwtoauthlogin.domain.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 스프링 시큐리티의 폼 기반의 UsernamePasswordAuthenticationFilter를 참고하여 만든 커스텀 필터
 * 거의 구조가 같고, Type이 Json인 Login만 처리하도록 설정한 부분만 다르다. (커스텀 API용 필터 구현)
 * Username : 회원 아이디 -> email로 설정
 * "/login" 요청 왔을 때 JSON 값을 매핑 처리하는 필터
 *
 *
 * /**
 *      * 필터 설정 및 초기화:
 *      *
 *      * CustomJsonUsernamePasswordAuthenticationFilter 클래스는 AbstractAuthenticationProcessingFilter 클래스를 상속하고,
 *      * /login 경로로의 POST 요청에 대해 동작하는 필터로 초기화됩니다.
 *      * 필터가 동작할 경로 및 HTTP 메서드는 상수로 지정되어 있습니다.
 *
 */
public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_LOGIN PATH_REQUEST_MATCHER =
    new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    private final ObjectMapper objectMapper;


    // login + POST 요청 처리

    /**
     * super()를 통해 부모 클래스인 AbstractAuthenticationProcessingFilter()의
     *
     * 생성자 파라미터로 위에서 상수로 선언한 "/login" URL을 설정해줬습니다.
     *
     * 이렇게 설정함으로써 우리가 만든 필터는 "/login" URL이 들어올 시 작동하게 됩니다.
     *
     *
     *
     * 또한, 생성자의 파라미터로 ObjectMapper를 받아 ObjectMapper를 생성자 주입해줬습니다.
     *
     * * 생성자:
     *  *      *
     *  *      * 필터 생성자에서는 ObjectMapper 객체를 주입받습니다. 이 ObjectMapper는 JSON 데이터를 자바 객체로 변환하는 데 사용됩니다.
     *  *
     *  *      */
     */
    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 처리 메소드
     *
     * UsernamePasswordAuthenticationFilter와 동일하게 UsernamePasswordAuthenticationToken 사용
     * StreamUtils를 통해 request에서 messageBody(JSON) 반환
     * 요청 JSON Example
     * {
     *    "email" : "aaa@bbb.com"
     *    "password" : "test123"
     * }
     * 꺼낸 messageBody를 objectMapper.readValue()로 Map으로 변환 (Key : JSON의 키 -> email, password)
     * Map의 Key(email, password)로 해당 이메일, 패스워드 추출 후
     * UsernamePasswordAuthenticationToken의 파라미터 principal, credentials에 대입
     *
     * AbstractAuthenticationProcessingFilter(부모)의 getAuthenticationManager()로 AuthenticationManager 객체를 반환 받은 후
     * authenticate()의 파라미터로 UsernamePasswordAuthenticationToken 객체를 넣고 인증 처리
     * (여기서 AuthenticationManager 객체는 ProviderManager -> SecurityConfig에서 설정)
     */

    /**
     *
     * attemptAuthentication 메서드:
     *
     * 이 메서드는 인증 시도를 처리하는 핵심 메서드입니다.
     * 먼저, 요청의 Content-Type을 확인하여 JSON 형식인지 검사합니다. Content-Type이 올바르지 않으면 AuthenticationServiceException을 던집니다.
     * 요청 본문(request body)에서 JSON 데이터를 추출합니다. 이 데이터는 사용자가 입력한 인증 정보를 포함하고 있습니다.
     * ObjectMapper를 사용하여 JSON 데이터를 Map<String, String> 형태로 변환합니다. 이 때, JSON 키 "email"과 "password"를 사용하여 매핑합니다.
     * 추출된 이메일과 패스워드를 사용하여 UsernamePasswordAuthenticationToken 객체를 생성합니다. 이 객체는 Spring Security에서 사용자 인증을 시도하는 데 사용됩니다.
     */

    /**
     *
     * 실제 인증 처리:
     *
     * UsernamePasswordAuthenticationToken 객체를 AuthenticationManager에 제출합니다.
     * AuthenticationManager는 Spring Security 구성에서 정의된 방식에 따라 사용자를 인증하고, 사용자의 권한 등을 확인합니다.
     * AbstractAuthenticationProcessingFilter의 getAuthenticationManager() 메서드를 통해 AuthenticationManager 객체를 가져옵니다.
     * authenticate(authRequest)를 호출하여 사용자를 실제로 인증하고, 인증이 성공하면 Authentication 객체를 반환합니다.
     */

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        String email = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
