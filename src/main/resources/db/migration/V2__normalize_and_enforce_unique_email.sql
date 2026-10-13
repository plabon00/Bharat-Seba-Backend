-- Normalize existing user emails and remove whitespace
UPDATE users 
SET email = LOWER(TRIM(email))
WHERE email != LOWER(TRIM(email));

-- Ensure unique index on normalized (lowercase and trimmed) email so duplicate emails cannot be registered
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique_normalized 
ON users (LOWER(TRIM(email)));
