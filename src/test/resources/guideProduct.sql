INSERT INTO guide_product (host_id, title, description, price, location, guide_start, guide_end)
VALUES (1, 'Example Title1', 'Example Description1', 10000, ST_GeomFromText('POINT(37.123 -122.456)', 4326),
        '2024-05-11 10:00:00', '2024-05-11 18:00:00'),
       (1, 'Example Title2', 'Example Description2', 10000, ST_GeomFromText('POINT(37.123 -122.456)', 4326),
        '2024-05-11 10:00:00', '2024-05-11 18:00:00');
