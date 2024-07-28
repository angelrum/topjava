DELETE FROM user_role;
DELETE FROM meal;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meal (user_id, date_time, description, calories)
VALUES (100001, '2024-01-01 10:34:13', 'Завтрак', 1000),
       (100001, '2024-01-01 13:00:00', 'Обед', 700),
       (100001, '2024-01-01 17:34:13', 'Ужин', 700);
