package com.example.jwtoauthlogin.domain.user.controller;

import com.example.jwtoauthlogin.domain.user.dto.UserSignUpDto;
import com.example.jwtoauthlogin.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            String msg = userService.signUp(userSignUpDto);
            return ResponseEntity.ok(msg);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
    }
}
