package com.example.BE.watchlist.entity;

import com.example.BE.entity.User;
import com.example.BE.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "watch_lists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_watch_list_user_stock",
                        columnNames = {"user_id", "stock_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 로그인 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 관심종목으로 등록한 종목
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    public WatchList(User user, Stock stock) {
        this.user = user;
        this.stock = stock;
    }
}
