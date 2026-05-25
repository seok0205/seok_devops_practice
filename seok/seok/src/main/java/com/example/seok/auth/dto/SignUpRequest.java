package com.example.seok.auth.dto;

import lombok.Getter;
import lombok.Setter;

// 회원가입 요청 DTO
@Getter @Setter
public class SignUpRequest {
    private String username;
    private String password;
}