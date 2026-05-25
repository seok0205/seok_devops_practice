package com.example.seok.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AuthDto {

    // 1. 회원가입 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {

        @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
        @Size(min = 4, max = 15, message = "ID는 4자 이상, 15자 이하이어야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영문자와 숫자만 사용 가능합니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다.")
        // 정규식: 최소 하나의 영문자, 숫자, 특수문자(@$!%*?&)를 포함해야 함
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
        )
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