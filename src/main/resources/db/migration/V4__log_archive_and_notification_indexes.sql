ALTER TABLE pipeline_runs ADD COLUMN log_archive_uri VARCHAR(1000);
ALTER TABLE pipeline_runs ADD COLUMN log_digest_sha256 VARCHAR(64);
ALTER TABLE pipeline_runs ADD COLUMN log_line_count INT NOT NULL DEFAULT 0;

CREATE INDEX idx_pipeline_runs_log_digest ON pipeline_runs(log_digest_sha256);
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, read_flag);
