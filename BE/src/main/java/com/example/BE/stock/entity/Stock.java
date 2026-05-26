package com.example.BE.stock.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker", nullable = false, unique = true, length = 50)
    private String ticker;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // KOSPI, KOSDAQ 등
    @Column(name = "market", nullable = false, length = 50)
    private String market;
}