-- 1. Create Teachers First
CREATE TABLE teachers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- 2. Create Quizzes
CREATE TABLE quizzes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    subject VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
);

-- 3. Create Questions
CREATE TABLE questions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,

    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- 4. Create Choices
CREATE TABLE choices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id BIGINT NOT NULL,
    choice_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 5. Create Sessions (The Live Rooms)
CREATE TABLE sessions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    join_code VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'WAITING', -- WAITING, ACTIVE, FINISHED
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,

    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- 6. Create Session Participants (Students in the room)
CREATE TABLE session_participants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    session_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    score INTEGER DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    
    -- Prevents a student from joining the exact same live session twice
    UNIQUE (session_id, username) 
);

-- 7. Create Answers (The historic record of clicks)
CREATE TABLE answers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    participant_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    choice_id BIGINT NOT NULL,
    wager INTEGER NOT NULL DEFAULT 0,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (participant_id) REFERENCES session_participants(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (choice_id) REFERENCES choices(id) ON DELETE CASCADE,
    
    -- Prevents a student from answering the same question twice!
    UNIQUE (participant_id, question_id) 
);