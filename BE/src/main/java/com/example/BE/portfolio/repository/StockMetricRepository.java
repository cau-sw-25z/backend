package com.example.BE.stock.repository;

import com.example.BE.stock.entity.StockMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockMetricRepository extends JpaRepository<StockMetric, Long> {

    Optional<StockMetric>
    findFirstByStock_IdAndAnnualVolatilityIsNotNullAndAnnualVolatilityGreaterThanOrderByDateDesc(
            Long stockId,
            Double minimumVolatility
    );
}