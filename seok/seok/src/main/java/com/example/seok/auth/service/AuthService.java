package com.example.seok.auth.service;

import com.example.seok.auth.dto.AuthDto.*;
import com.example.seok.auth.entity.User;
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

    /**
     * 회원가입 비즈니스 로직
     */
    @Transactional // 쓰기 작업이 일어나므로 별도로 선언
    public SignUpResponse signUp(SignUpRequest request) {
        // 1. 중복 유저 검증
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }

        // 2. 비밀번호 암호화 (BCrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 유저 엔티티 생성 및 저장
        User user = new User(request.getUsername(), encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("회원가입 완료: username = {}", savedUser.getUsername());

        // 4. 응답 DTO 반환
        return new SignUpResponse(savedUser.getId(), savedUser.getUsername());
    }

    /**
     * 로그인 비즈니스 로직 (MySQL 조회 및 Redis 세션 토큰 저장)
     */
    public String login(LoginRequest request) {
        // 1. MySQL에서 유저 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. BCrypt 암호화 비밀번호 검증 (matches 사용)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치 -> 사용자: {}", request.getUsername());
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 3. 로그인 성공 -> 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();
        log.info("로그인 성공: 사용자 = {}, 발급된 토큰 = {}", user.getUsername(), token);

        // 4. Redis에 토큰 저장 (유효기간 30분 설정)
        redisTemplate.opsForValue().set(
                "TOKEN:" + token,
                user.getUsername(),
                30,
                TimeUnit.MINUTES
        );

        return token;
    }
}