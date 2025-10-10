DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requests;
DELETE FROM users;

INSERT INTO users (id, name, email) VALUES
(1, 'Test User 1', 'test1@example.com'),
(2, 'Test User 2', 'test2@example.com'),
(53, 'Booker User', 'booker@example.com');

INSERT INTO items (id, name, description, is_available, owner_id, request_id) VALUES
(18, 'paVOpdG9rp', 'Test item for comments', true, 1, NULL);

INSERT INTO bookings (id, start_date, end_date, item_id, booker_id, status) VALUES
(9, CURRENT_TIMESTAMP + INTERVAL 1 DAY, CURRENT_TIMESTAMP + INTERVAL 3 DAY, 18, 53, 'APPROVED'),
(18, CURRENT_TIMESTAMP + INTERVAL 5 DAY, CURRENT_TIMESTAMP + INTERVAL 7 DAY, 18, 53, 'APPROVED');

INSERT INTO comments (id, text, item_id, author_id, created) VALUES
(1, 'Test comment', 18, 53, CURRENT_TIMESTAMP);