-- Flyway Migration V1: Initial schema creation

-- Create tasks table
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create index on status for faster filtering
CREATE INDEX idx_tasks_status ON tasks(status);

-- Create index on created_at for ordering (most recent first)
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

-- Add check constraint for valid status values
ALTER TABLE tasks ADD CONSTRAINT tasks_status_check
    CHECK (status IN ('TO_DO', 'IN_PROGRESS', 'DONE'));
