INSERT INTO users (id, name, email, password_hash, role, enabled) VALUES
('00000000-0000-0000-0000-000000000001', 'Admin User', 'admin@buildsage.dev', '$2a$10$YcPa5taQwJb7GnFRpa7mDeKoFA4ONjmjMtcHpQBh6/3mDxOQudgI6', 'ADMIN', true),
('00000000-0000-0000-0000-000000000002', 'Developer User', 'dev@buildsage.dev', '$2a$10$YcPa5taQwJb7GnFRpa7mDeKoFA4ONjmjMtcHpQBh6/3mDxOQudgI6', 'DEVELOPER', true),
('00000000-0000-0000-0000-000000000003', 'Viewer User', 'viewer@buildsage.dev', '$2a$10$YcPa5taQwJb7GnFRpa7mDeKoFA4ONjmjMtcHpQBh6/3mDxOQudgI6', 'VIEWER', true),
('00000000-0000-0000-0000-000000000004', 'Other Team Developer', 'other-dev@buildsage.dev', '$2a$10$YcPa5taQwJb7GnFRpa7mDeKoFA4ONjmjMtcHpQBh6/3mDxOQudgI6', 'DEVELOPER', true);

INSERT INTO teams (id, name) VALUES
('10000000-0000-0000-0000-000000000001', 'Platform Engineering'),
('10000000-0000-0000-0000-000000000002', 'Data Engineering');

INSERT INTO team_members (team_id, user_id, role) VALUES
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'ADMIN'),
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'DEVELOPER'),
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'VIEWER'),
('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', 'DEVELOPER');

INSERT INTO projects (id, team_id, name, description) VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Payments API', 'Demo service used for CI/CD intelligence examples.'),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'Analytics API', 'Second demo service for authorization tests.');

INSERT INTO repositories (project_id, provider, url, default_branch) VALUES
('20000000-0000-0000-0000-000000000001', 'github', 'https://github.com/example/payments-api', 'main');
