package com.example.BE.watchlist.repository;

import com.example.BE.watchlist.entity.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    List<WatchList> findByUser_IdOrderByIdDesc(Long userId);

    boolean existsByUser_IdAndStock_Id(Long userId, Long stockId);

    Optional<WatchList> findByUser_IdAndStock_Ticker(Long userId, String ticker);
}