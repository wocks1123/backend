-- reservation 테이블에 데이터 삽입
INSERT INTO `reservation` (`id`, `created_at`, `created_by`, `deleted_at`, `deleted_by`, `updated_at`, `updated_by`,
                           `cancelled_at`, `imp_uid`, `merchant_uid`, `message`, `paid_at`, `payment_status`,
                           `personnel`, `price`, `reservated_at`, `reservation_status`, `client_id`, `guide_id`,
                           `product_id`)
VALUES (1, '2024-04-29 21:15:07', NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_1', 'merchant_uid_1', '예약 메시지 1',
        '2024-04-29 21:15:07', 1, 2, 10000, '2024-04-30 10:00:00', 0, 1, 2, 1),
       (2, '2024-04-29 21:15:07', NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_2', 'merchant_uid_2', '예약 메시지 2',
        '2024-04-29 21:15:07', 1, 3, 15000, '2024-05-01 14:00:00', 1, 1, 2, 1),
       (3, '2024-04-29 21:15:07', NULL, NULL, NULL, NULL, NULL, NULL, 'imp_uid_3', 'merchant_uid_3', '예약 메시지 3',
        '2024-04-29 21:15:07', 1, 1, 8000, '2024-05-02 15:30:00', 1, 2, 1, 2);
