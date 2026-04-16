-- ─────────────────────────────────────────────────────────────────────────────
-- Employee Service – Seed Data
-- ──────��──────────────────────────────────────────────────────────────────────

INSERT INTO employees (first_name, middle_name, last_name, last_mother_name, age, sex, birth_date, position, status)
VALUES
    ('John',   'Paul',    'Doe',      'Smith',    '34', 'M', '1990-03-15', 'Software Engineer',  TRUE),
    ('Maria',  NULL,      'Garcia',   'Lopez',    '28', 'F', '1996-07-22', 'Product Manager',    TRUE),
    ('Carlos', 'Antonio', 'Martinez', 'Ramirez',  '45', 'M', '1979-11-08', 'DevOps Engineer',    TRUE),
    ('Laura',  'Isabel',  'Torres',   'Mendoza',  '31', 'F', '1993-05-30', 'QA Engineer',        FALSE);

