package com.example.seok.auth.service;

import com.example.seok.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 인증번호 발송 및 Redis 저장
    public void sendVerificationCode(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일 주소가 올바르지 않습니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 주소입니다.");
        }

        // 6자리 랜덤 번호 생성
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 네이버 SMTP를 이용해 메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("[예약시스템] 매니저 가입 인증번호입니다.");
        message.setText("인증번호: " + code + "\n3분 이내에 입력해주세요.");
        mailSender.send(message);

        // Redis에 [EMAIL:이메일주소]를 키로 하여 인증번호를 3분간 저장
        redisTemplate.opsForValue().set(
                "EMAIL:" + email,
                code,
                3,
                TimeUnit.MINUTES
        );
    }

    // 사용자가 입력한 인증번호 검증 및 소멸
    public boolean verifyCode(String email, String code) {
        String redisKey = "EMAIL:" + email;
        String savedCode = redisTemplate.opsForValue().get(redisKey);

        if (savedCode == null) {
            throw new IllegalArgumentException("인증 시간이 만료되었거나 발송 이력이 없습니다.");
        }

        if (savedCode.equals(code)) {
            redisTemplate.delete(redisKey); // 검증 성공 시 즉시 삭제 처리
            return true;
        }

        return false;
    }
}