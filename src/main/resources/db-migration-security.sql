-- Database migration script for security features
-- Add security fields to bs_user table

ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER NOT NULL DEFAULT 0;
ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS is_locked BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS lock_expiration_time BIGINT;
ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS last_login BIGINT;
ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(255) UNIQUE;
ALTER TABLE bs_user ADD COLUMN IF NOT EXISTS reset_token_expiration BIGINT;

-- Create audit log table
CREATE TABLE IF NOT EXISTS bs_audit_log (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp BIGINT NOT NULL,
    user_agent TEXT,
    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES bs_user(id) ON DELETE CASCADE
);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_audit_log_user_timestamp ON bs_audit_log(user_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_audit_log_user_action ON bs_audit_log(user_id, action);

