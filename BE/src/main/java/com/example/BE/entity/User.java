package com.example.BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 255)
    private String nickname;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User(String email, String nickname, String passwordHash) {
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }

    public void updateRiskLevel(String riskLevel) {
    this.riskLevel = riskLevel;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}