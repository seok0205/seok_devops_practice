package com.example.seok.service;

import com.example.seok.entity.User;
import com.example.seok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public String login(String username, String password) {
        // 1. MySQL에서 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 비밀번호 검증 (실무에선 암호화 필수, 우선 평문 비교)
        if (!user.getPassword().equals(password)) {
            log.warn("로그인 실패: 비밀번호 불일치 -> 사용자: {}", username);
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 3. 로그인 성공 -> 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();
        log.info("로그인 성공: 사용자 = {}, 발급된 토큰 = {}", username, token);

        // 4. Redis에 토큰 저장 (유효기간 30분 설정으로 메모리 고갈 방지)
        redisTemplate.opsForValue().set(
                "TOKEN:" + token,
                user.getUsername(),
                30,
                TimeUnit.MINUTES
        );

        return token;
    }
}