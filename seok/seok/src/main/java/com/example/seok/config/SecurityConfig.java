package com.example.seok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화 빈 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 로컬 테스트 및 API 서버용이므로 CSRF 보호는 끔
                .csrf(csrf -> csrf.disable())
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger 관련 주소는 인증 없이 누구나 접근 허용 (화면 대체재)
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 2. 데브옵스 모니터링용 Actuator 엔드포인트도 인증 없이 접근 허용
                        // (나중에 프로메테우스가 긁어갈 수 있도록)
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 3. 그 외의 모든 일반 API 요청은 인증(로그인)을 받도록 설정
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}