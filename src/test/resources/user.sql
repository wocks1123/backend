-- user 테이블에 데이터 삽입
INSERT INTO `user` (`id`, `created_at`, `created_by`, `deleted_at`, `deleted_by`, `updated_at`, `updated_by`, `account`,
                    `email`, `password`)
VALUES (1, '2024-04-29 21:15:07', NULL, NULL, NULL, NULL, NULL, 'user1', 'user1@example.com', 'password1'),
       (2, '2024-04-29 21:15:07', NULL, NULL, NULL, NULL, NULL, 'user2', 'user2@example.com', 'password2');