package com.example.seok.auth.repository;

import com.example.seok.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // 중복 가입 방지를 위한 username 존재 여부 확인 메서드
    boolean existsByUsername(String username);
}