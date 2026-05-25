package com.example.seok.auth.controller;

import com.example.seok.auth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "MySQL 및 Redis 연동 로그인 테스트")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "ID/PW 검증 후 Redis에 세션 토큰을 생성합니다.")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("로그인 성공! 발급된 토큰: " + token);
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}