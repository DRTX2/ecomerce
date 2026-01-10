-- Create refresh_tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
);

-- Create index on user_email for faster lookups
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_email ON refresh_tokens(user_email);

-- Create index on token for faster lookups
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);

