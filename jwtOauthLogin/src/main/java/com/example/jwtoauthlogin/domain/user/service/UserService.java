package com.example.jwtoauthlogin.domain.user.service;

import com.example.jwtoauthlogin.domain.user.domain.Role;
import com.example.jwtoauthlogin.domain.user.domain.User;
import com.example.jwtoauthlogin.domain.user.dto.UserSignUpDto;
import com.example.jwtoauthlogin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String signUp(UserSignUpDto userSignUpDto) {
        if (!StringUtils.hasText(userSignUpDto.getEmail()) || !StringUtils.hasText(userSignUpDto.getPassword())
            || !StringUtils.hasText(userSignUpDto.getNickname())) {
            throw new IllegalArgumentException("필수 입력 정보가 누락되었습니다.");
        }

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            return "이미 존재하는 닉네임입니다.";
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .age(userSignUpDto.getAge())
                .city(userSignUpDto.getCity())
                .role(Role.USER)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
        return "회원가입이 성공적으로 완료되었습니다.";
    }
}
