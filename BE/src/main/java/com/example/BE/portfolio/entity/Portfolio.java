package com.example.BE.portfolio.entity;

import com.example.BE.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "portfolios")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /*
     * 프로젝트 확정 컨벤션
     * 1 = 공격형
     * 5 = 안정형
     */
    @Column(name = "risk_level", nullable = false)
    private Integer riskLevel;

    public Portfolio(User user, String name, Integer riskLevel) {
        this.user = user;
        this.name = name;
        this.riskLevel = riskLevel;
    }

    public void rename(String name) {
        this.name = name;
    }
}