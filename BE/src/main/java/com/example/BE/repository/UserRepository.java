package com.example.BE.repository;

import com.example.BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// User 엔티티에 대한 DB 접근을 담당하는 Repository
// JpaRepository<User, Long>을 상속하면 기본 CRUD 메서드를 자동으로 사용할 수 있음
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자를 조회하는 메서드
    // Spring Data JPA가 메서드 이름을 보고 자동으로 쿼리를 생성함
    Optional<User> findByEmail(String email);
}  