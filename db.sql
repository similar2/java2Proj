DROP TABLE IF EXISTS question,"user",answer,activity;

-- Table for storing questions
CREATE TABLE IF NOT EXISTS question
(
    id                 SERIAL PRIMARY KEY, -- Auto-incremented primary key
    tags               TEXT,               -- Tags as a text field
    owner_account_id   VARCHAR(255),       -- Owner's account ID
    owner_reputation   VARCHAR(255),       -- Owner's reputation (stored as a string)
    owner_user_id      VARCHAR(255),       -- Owner's user ID
    owner_display_name VARCHAR(255),       -- Owner's display name
    is_answered        BOOLEAN,            -- Whether the question is answered
    view_count         BIGINT,             -- Number of views
    answer_count       BIGINT,             -- Number of answers
    score              BIGINT,             -- Question score
    last_activity_date BIGINT,             -- Last activity date (stored as epoch timestamp)
    creation_date      BIGINT,             -- Creation date (stored as epoch timestamp)
    question_id        BIGINT UNIQUE,      -- Question ID (unique identifier for the question)
    title              TEXT,               -- Title of the question
    content            VARCHAR
);

-- Table for storing user information
CREATE TABLE IF NOT EXISTS "user"
(
    id               SERIAL PRIMARY KEY, -- Auto-incremented primary key
    badge_counts     BIGINT,             -- Number of badges the user has
    account_id       BIGINT UNIQUE,      -- Network-wide account ID
    is_employee      BOOLEAN,            -- Whether the user is an employee
    last_access_date BIGINT,             -- Last access date (stored as epoch timestamp)
    reputation       BIGINT,             -- User's reputation
    creation_date    BIGINT,             -- Account creation date (stored as epoch timestamp)
    user_id          BIGINT UNIQUE       -- Site-specific user ID
);

-- Table for storing answers
CREATE TABLE IF NOT EXISTS answer
(
    id                 SERIAL PRIMARY KEY, -- Auto-incremented primary key
    owner_account_id   VARCHAR(255),       -- Owner's account ID
    owner_reputation   VARCHAR(255),       -- Owner's reputation (stored as a string)
    owner_user_id      VARCHAR(255),       -- Owner's user ID
    owner_display_name VARCHAR(255),       -- Owner's display name
    is_accepted        BOOLEAN,            -- Whether the answer was accepted
    score              BIGINT,             -- Answer score
    last_activity_date BIGINT,             -- Last activity date (stored as epoch timestamp)
    creation_date      BIGINT,             -- Creation date (stored as epoch timestamp)
    answer_id          BIGINT UNIQUE,      -- Answer ID (unique identifier for the answer)
    question_id        BIGINT,             -- ID of the question this answer belongs to
    body               TEXT                -- Body/content of the answer
);
CREATE TABLE IF NOT EXISTS activity
(
    id              SERIAL PRIMARY KEY,
    question_id     BIGINT,                -- Assumes question IDs are stored as BIGINT
    user_id         BIGINT,                -- Assumes user IDs are stored as BIGINT
    activity_type   VARCHAR(255) NOT NULL, -- Check constraint for valid activity types
    user_reputation BIGINT
);