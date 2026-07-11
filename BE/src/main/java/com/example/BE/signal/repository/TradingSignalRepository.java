package com.example.BE.signal.repository;

import com.example.BE.signal.entity.TradingSignal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TradingSignalRepository extends JpaRepository<TradingSignal, Long> {

    @Query("SELECT MAX(ts.signalDate) FROM TradingSignal ts")
    LocalDate findLatestSignalDate();

    List<TradingSignal> findBySignalDateOrderByTickerAscIdAsc(LocalDate signalDate);

    List<TradingSignal> findByTickerOrderBySignalDateDescCreatedAtDesc(
            String ticker,
            Pageable pageable
    );
}