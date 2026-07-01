ALTER TABLE pipeline_runs ADD COLUMN idempotency_key VARCHAR(255);

CREATE UNIQUE INDEX uk_pipeline_runs_project_idempotency
    ON pipeline_runs(project_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;

CREATE INDEX idx_pipeline_runs_project_status_created
    ON pipeline_runs(project_id, status, created_at DESC);

CREATE INDEX idx_pipeline_logs_run_content_lower
    ON pipeline_logs(pipeline_run_id, lower(content));

CREATE INDEX idx_deployments_project_risk
    ON deployments(project_id, risk_score);
