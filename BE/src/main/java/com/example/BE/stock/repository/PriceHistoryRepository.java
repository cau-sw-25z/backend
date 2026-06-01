package com.example.BE.stock.repository;

import com.example.BE.stock.entity.PriceHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // 최근 N개 (정렬은 date 내림차순)
    List<PriceHistory> findByStockIdOrderByDateDesc(Long stockId, Pageable pageable);

    // 날짜 범위 조회 (오름차순 — 차트용)
    List<PriceHistory> findByStockIdAndDateBetweenOrderByDateAsc(
            Long stockId, LocalDate from, LocalDate to);
}