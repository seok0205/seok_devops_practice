package com.example.seok.auth.service;

import com.example.seok.auth.dto.AuthDto.*;
import com.example.seok.auth.entity.User;
import com.example.seok.auth.entity.enums.UserRole;
import com.example.seok.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용을 기본으로 설정하여 성능 최적화
public class AuthService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    /**
     * 회원가입 비즈니스 로직
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }

        if (request.getRole() == UserRole.MANAGER || request.getEmail() != null) {

            if (request.getRole() == UserRole.MANAGER &&
                    (request.getEmail() == null || request.getVerificationCode() == null)) {
                throw new IllegalArgumentException("매니저 가입은 메일 인증이 필수입니다.");
            }

            if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 가입된 이메일 주소입니다.");
            }

            if (request.getRole() == UserRole.MANAGER) {
                boolean isVerified = emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode());
                if (!isVerified) {
                    throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
                }
            }
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), encodedPassword, request.getRole(), request.getEmail());
        User savedUser = userRepository.save(user);

        log.info("회원가입 완료: username = {}, role = {}, email = {}", savedUser.getUsername(), savedUser.getRole(), savedUser.getEmail());

        return new SignUpResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    /**
     * 로그인 비즈니스 로직 (MySQL 조회 및 Redis 세션 토큰 저장)
     */
    public String login(LoginRequest request) {
        // MySQL에서 유저 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // BCrypt 암호화 비밀번호 검증 (matches 사용)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치 -> 사용자: {}", request.getUsername());
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 로그인 성공 -> 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();
        log.info("로그인 성공: 사용자 = {}, 발급된 토큰 = {}", user.getUsername(), token);

        // Redis에 토큰 저장 (유효기간 30분)
        String redisValue = user.getUsername() + ":" + user.getRole().name();
        redisTemplate.opsForValue().set(
                "TOKEN:" + token,
                redisValue,
                30,
                TimeUnit.MINUTES
        );

        return token;
    }
}