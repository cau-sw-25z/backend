package com.example.BE.repository;

import com.example.BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// User 엔티티에 대한 DB 접근을 담당하는 Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 회원가입 시 이메일 중복 확인
    boolean existsByEmail(String email);
}