INSERT INTO guide_product (host_id, title, description, price, location, guide_start, guide_end, guide_thumbnail,
                           guide_images, guide_time, guide_start_time, guide_end_time, location_name)
VALUES (1, 'Example Title1', 'Example Description1', 20000, ST_GeomFromText('POINT(37.123 -122.456)', 4326),
        '2024-05-11 15:00:00', '2024-05-11 14:59:59', 'thumbnail', '["image1", "image2"]', 3, '10:00:00', '15:00:00',
        '서울'),
       (2, 'Example Title2', 'Example Description2', 20000, ST_GeomFromText('POINT(37.123 -122.456)', 4326),
        '2024-05-11 15:00:00', '2024-05-11 14:59:59', 'thumbnail', '["image1", "image2"]', 3, '10:00:00', '15:00:00',
        '서울');

INSERT INTO guide_category (product_id, category_code)
VALUES (1, 'OUTDOOR'),
       (1, 'TOUR'),
       (2, 'DINING'),
       (2, 'OUTDOOR');
