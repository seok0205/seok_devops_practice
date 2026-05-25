package com.example.seok.auth.controller;

import com.example.seok.auth.dto.AuthDto.*;
import com.example.seok.auth.service.AuthService;
import com.example.seok.auth.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "MySQL 및 Redis 연동 회원가입/로그인")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            SignUpResponse response = authService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/signup-mail")
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody EmailRequest request) {
        // AuthService를 거치지 않고 직접 호출하여 DB 트랜잭션 락 유발 가능성을 원천 차단
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증번호가 발송되었습니다. 3분 이내에 확인해주세요.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok("로그인 성공! 발급된 토큰: " + token);
    }
}