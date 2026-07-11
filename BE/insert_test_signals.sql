SET NAMES utf8mb4;

SET @stock_id := (SELECT id FROM stocks WHERE ticker = '005930' LIMIT 1);

INSERT INTO trading_signals
(stock_id, ticker, strategy_type, action, signal_value, close_price, signal_date, created_at)
VALUES
    (@stock_id, '005930', 'TEST', '신규 매수 진입', 1.0, 73500.00, CURDATE(), NOW()),
    (@stock_id, '005930', 'TEST', '상한가 도달', 1.0, 73500.00, CURDATE(), NOW()),
    (@stock_id, '005930', 'TEST', '전량 매도 청산', -1.0, 73500.00, CURDATE(), NOW()),
    (@stock_id, '005930', 'TEST', '50% 부분 익절', -0.5, 73500.00, CURDATE(), NOW()),
    (@stock_id, '005930', 'TEST', '과열 익절 청산', -1.5, 73500.00, CURDATE(), NOW()),
    (@stock_id, '005930', 'TEST', '긴급 손절', -2.0, 73500.00, CURDATE(), NOW());