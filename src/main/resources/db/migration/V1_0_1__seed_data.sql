INSERT INTO users (user_id, username, email, password_hash, created_at, updated_at)
VALUES
  -- 'SuperSecurePassword2026'
  (1, 'lucas_dev', 'lucas@gcash.com', '$2a$10$B5s1yJxl0mUXD7oHjI7PzuQn7Ksp1i7d.yN5Xl6c9aRtK.1kS2QnC', NOW(), NOW()),
  -- 'UserPassword2026'
  (2, 'khanh_user', 'khanh@gmail.com', '$2a$10$Z1eO7hY7kP3Q9zR5lS2J.O3d8mX7F9zR2wO5lS2J.O3d8mX7F9zR2', NOW(), NOW()),
  -- 'BotPassword2026'
  (3, 'system_bot', 'bot@gcash.com', '$2a$10$v5R2lS2J.O3d8mX7F9zR2wO5lS2J.O3d8mX7F9zR2wO5lS2J.O3d8', NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('users', 'user_id'), COALESCE(MAX(user_id), 0) + 1, false) FROM users;