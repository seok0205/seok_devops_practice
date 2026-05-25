package com.example.seok.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDto {

    // 1. 회원가입 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {
        private String username;
        private String password;
    }

    // 2. 회원가입 응답 DTO
    @Getter
    @AllArgsConstructor
    public static class SignUpResponse {
        private Long id;
        private String username;
    }

    // 3. 로그인 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }
}