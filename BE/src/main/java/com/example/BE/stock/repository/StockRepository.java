package com.example.BE.stock.repository;

import com.example.BE.stock.entity.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByTicker(String ticker);

    /**
     * 커서 기반 페이지네이션 + 키워드 검색
     * 키워드가 null이면 전체 조회, 있으면 ticker 또는 name에 LIKE 검색
     * cursorId보다 큰 id만 조회 → id 오름차순 정렬
     */
    @Query("""
        SELECT s FROM Stock s
        WHERE s.id > :cursorId
          AND (:keyword IS NULL
               OR LOWER(s.ticker) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.name)   LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY s.id ASC
    """)
    List<Stock> findStocksWithCursor(@Param("cursorId") Long cursorId,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);
}