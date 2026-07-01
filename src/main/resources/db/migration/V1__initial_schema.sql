CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN','DEVELOPER','VIEWER')),
    enabled BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE team_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN','DEVELOPER','VIEWER')),
    UNIQUE(team_id, user_id)
);

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);
CREATE INDEX idx_projects_team ON projects(team_id);
CREATE INDEX idx_projects_name ON projects(lower(name));

CREATE TABLE repositories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    provider VARCHAR(64) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    default_branch VARCHAR(255) NOT NULL
);

CREATE TABLE pipeline_runs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    external_id VARCHAR(255) NOT NULL,
    branch VARCHAR(255) NOT NULL,
    commit_sha VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('SUCCESS','FAILED','RUNNING','CANCELED','UNKNOWN')),
    started_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ
);
CREATE INDEX idx_pipeline_runs_project_created ON pipeline_runs(project_id, created_at DESC);
CREATE INDEX idx_pipeline_runs_status ON pipeline_runs(status);

CREATE TABLE pipeline_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    stage VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('SUCCESS','FAILED','RUNNING','CANCELED','UNKNOWN'))
);

CREATE TABLE pipeline_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    line_number INT NOT NULL,
    content VARCHAR(4000) NOT NULL,
    UNIQUE(pipeline_run_id, line_number)
);
CREATE INDEX idx_pipeline_logs_run_line ON pipeline_logs(pipeline_run_id, line_number);

CREATE TABLE test_failures (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    test_class VARCHAR(255) NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    error_message VARCHAR(4000) NOT NULL
);

CREATE TABLE deployments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    environment VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('PENDING','SUCCESS','FAILED','ROLLED_BACK')),
    deployed_at TIMESTAMPTZ NOT NULL,
    risk_score INT NOT NULL DEFAULT 0 CHECK (risk_score BETWEEN 0 AND 100)
);
CREATE INDEX idx_deployments_project_created ON deployments(project_id, created_at DESC);

CREATE TABLE incidents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    severity VARCHAR(16) NOT NULL CHECK (severity IN ('SEV1','SEV2','SEV3','SEV4')),
    status VARCHAR(32) NOT NULL CHECK (status IN ('OPEN','MITIGATED','RESOLVED')),
    postmortem_draft VARCHAR(8000)
);
CREATE INDEX idx_incidents_project_status ON incidents(project_id, status);

CREATE TABLE incident_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    incident_id UUID NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    occurred_at TIMESTAMPTZ NOT NULL,
    message VARCHAR(4000) NOT NULL
);

CREATE TABLE release_notes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    version VARCHAR(255) NOT NULL,
    body VARCHAR(8000) NOT NULL
);

CREATE TABLE ai_analyses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL CHECK (status IN ('QUEUED','PROCESSING','COMPLETED','FAILED')),
    failure_type VARCHAR(64) NOT NULL CHECK (failure_type IN ('TEST_FAILURE','BUILD_FAILURE','DOCKER_FAILURE','DEPENDENCY_FAILURE','ENVIRONMENT_FAILURE','DEPLOYMENT_FAILURE','UNKNOWN')),
    failed_stage VARCHAR(255) NOT NULL,
    root_cause_summary VARCHAR(8000) NOT NULL,
    confidence_score DOUBLE PRECISION NOT NULL CHECK (confidence_score BETWEEN 0 AND 1),
    evidence_lines VARCHAR(8000) NOT NULL,
    review_state VARCHAR(32) NOT NULL CHECK (review_state IN ('REVIEW_REQUIRED','APPROVED')),
    error_message VARCHAR(1000)
);
CREATE INDEX idx_ai_analyses_run_created ON ai_analyses(pipeline_run_id, created_at DESC);

CREATE TABLE background_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    type VARCHAR(255) NOT NULL,
    target_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('QUEUED','PROCESSING','COMPLETED','FAILED')),
    message VARCHAR(1000)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    user_id UUID NOT NULL,
    channel VARCHAR(255) NOT NULL,
    message VARCHAR(4000) NOT NULL,
    read_flag BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    actor_user_id UUID,
    action VARCHAR(255) NOT NULL,
    resource_type VARCHAR(255) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    details VARCHAR(4000) NOT NULL
);
CREATE INDEX idx_audit_logs_actor_created ON audit_logs(actor_user_id, created_at DESC);
