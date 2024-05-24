INSERT INTO user (email, nickname, name, profile, profile_image_url, phone, nationality, birthdate, gender,
                  password, sign_up_type, user_role, created_at)
VALUES ('example1@email.com', 'example_nickname1', 'name1', 'Profile test....', '/profile.jpg', '+01234567801',
        'KOR', '1990-01-01', 'MALE', '$2a$10$BHQwRk.uCuKxVS0qr4TyX.QvH5F8n6xFa3dsG0QYuE7nS1HZCeEma', 'LOCAL', 'USER',
        NOW()),
       ('example2@email.com', 'example_nickname2', 'name2', 'Profile test....', '/profile.jpg', '+01234567802',
        'KOR', '1990-01-02', 'MALE', 'password123', 'LOCAL', 'USER', NOW());

INSERT INTO user_language (user_id, language)
VALUES (1, 'ko'),
       (1, 'en'),
       (2, 'ko'),
       (2, 'zh');
