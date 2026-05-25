package com.example.seok.auth.dto;

import lombok.Getter;

// 회원가입 응답 DTO (비밀번호 제외)
@Getter
public class SignUpResponse {
    private Long id;
    private String username;

    public SignUpResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}