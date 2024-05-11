-- reservation 테이블에 데이터 삽입
INSERT INTO `reservation` (`id`, `created_at`, `created_by`, `deleted_at`, `deleted_by`, `updated_at`, `updated_by`,
                           `cancelled_at`, `imp_uid`, `merchant_uid`, `message`, `paid_at`, `payment_status`,
                           `personnel`, `price`, `reserved_at`, `reservation_status`, `client_id`, `guide_id`,
                           `product_id`)
VALUES (1, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_1', 'merchant_uid_1', '예약 메시지 1',
        NOW(), 1, 2, 10000, DATE_SUB(NOW(), INTERVAL 12 HOUR), 0, 1, 2, 1),
       (2, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_2', 'merchant_uid_2', '예약 메시지 2',
        NOW(), 1, 3, 15000, DATE_SUB(NOW(), INTERVAL 8 HOUR), 1, 1, 2, 1),
       (3, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_3', 'merchant_uid_3', '예약 메시지 3',
        NOW(), 1, 1, 8000, DATE_SUB(NOW(), INTERVAL 4 HOUR), 1, 2, 1, 2),
       (4, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_4', 'merchant_uid_4', '예약 메시지 4', NOW(),
        1, 2, 10000, DATE_ADD(NOW(), INTERVAL 4 HOUR), 0, 1, 2, 1),
       (5, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_5', 'merchant_uid_5', '예약 메시지 5', NOW(),
        1, 3, 15000, DATE_ADD(NOW(), INTERVAL 8 HOUR), 1, 1, 2, 1),
       (6, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_6', 'merchant_uid_6', '예약 메시지 6', NOW(),
        1, 1, 8000, DATE_ADD(NOW(), INTERVAL 12 HOUR), 1, 2, 1, 2);
