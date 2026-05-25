package com.example.BE.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_survey_responses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 어떤 사용자의 설문 응답인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 총점
    @Column(name = "total_score", nullable = false)
    private int totalScore;

    // 산출된 투자 성향 등급
    @Column(name = "risk_level", nullable = false, length = 50)
    private String riskLevel;

    // 응답 저장 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserSurveyResponse(User user, int totalScore, String riskLevel) {
        this.user = user;
        this.totalScore = totalScore;
        this.riskLevel = riskLevel;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}