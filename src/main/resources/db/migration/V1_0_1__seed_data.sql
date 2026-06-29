INSERT INTO users (user_id, username, email, password_hash, created_at, updated_at)
VALUES
  (1, 'lucas_dev', 'lucas@gcash.com', 'SuperSecurePassword2026', NOW(), NOW()),
  (2, 'khanh_user', 'khanh@gmail.com', 'UserPassword2026', NOW(), NOW()),
  (3, 'system_bot', 'bot@gcash.com', 'BotPassword2026', NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('users', 'user_id'), COALESCE(MAX(user_id), 0) + 1, false) FROM users;