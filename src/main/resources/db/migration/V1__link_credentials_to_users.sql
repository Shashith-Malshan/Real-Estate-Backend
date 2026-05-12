-- Migration: link user_credentials rows to users by matching username to user email
-- Run this after backup. This is a best-effort migration and assumes username == user.email for existing rows.

ALTER TABLE user_credentials ADD COLUMN IF NOT EXISTS user_id BIGINT;

UPDATE user_credentials uc
JOIN users u ON u.email = uc.username
SET uc.user_id = u.user_id
WHERE uc.user_id IS NULL;

-- Optionally add FK constraint after verifying data integrity:
-- ALTER TABLE user_credentials
--   ADD CONSTRAINT fk_user_credentials_user FOREIGN KEY (user_id) REFERENCES users(user_id);

