CREATE TABLE hashtag (
    id BIGINT NOT NULL PRIMARY KEY,
    hashtag TEXT NOT NULL
);

CREATE TABLE profile (
    id BIGINT NOT NULL PRIMARY KEY,
    username TEXT NOT NULL,
    full_name TEXT NOT NULL,
    picture_url TEXT NOT NULL
);

CREATE TABLE quiz (
    id BIGINT NOT NULL PRIMARY KEY,
    question TEXT NOT NULL,
    correct_tally_id BIGINT,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_quiz_owner FOREIGN KEY(owner_id) REFERENCES profile(id)
);

CREATE TABLE pool (
    id BIGINT NOT NULL PRIMARY KEY,
    question TEXT NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_pool_owner FOREIGN KEY(owner_id) REFERENCES profile(id)
);

CREATE TABLE quiz_hashtag (
    quiz_id BIGINT NOT NULL,
    hashtag_id BIGINT NOT NULL,
    PRIMARY KEY (quiz_id, hashtag_id),
    CONSTRAINT fk_quiz_hashtag_quiz FOREIGN KEY(quiz_id) REFERENCES quiz(id),
    CONSTRAINT fk_quiz_hashtag_hashtag FOREIGN KEY(hashtag_id) REFERENCES hashtag(id)
);

CREATE TABLE pool_hashtag (
    pool_id BIGINT NOT NULL,
    hashtag_id BIGINT NOT NULL,
    PRIMARY KEY (pool_id, hashtag_id),
    CONSTRAINT fk_pool_hashtag_pool FOREIGN KEY(pool_id) REFERENCES pool(id),
    CONSTRAINT fk_pool_hashtag_hashtag FOREIGN KEY(hashtag_id) REFERENCES hashtag(id)
);

CREATE TABLE quiz_tally (
    id SERIAL NOT NULL PRIMARY KEY,
    text TEXT NOT NULL,
    quiz_id BIGINT NOT NULL,
    CONSTRAINT quiz_tally_unique_constraint UNIQUE (text, quiz_id),
    CONSTRAINT fk_quiz_tally_quiz FOREIGN KEY(quiz_id) REFERENCES quiz(id)
);

CREATE TABLE pool_tally (
    id SERIAL NOT NULL PRIMARY KEY,
    text TEXT NOT NULL,
    pool_id BIGINT NOT NULL,
    CONSTRAINT pool_tally_unique_constraint UNIQUE (text, pool_id),
    CONSTRAINT fk_pool_tally_pool FOREIGN KEY(pool_id) REFERENCES pool(id)
);

ALTER TABLE quiz
ADD CONSTRAINT fk_quiz_tally
FOREIGN KEY(correct_tally_id) REFERENCES quiz_tally(id);

CREATE TABLE answer (
    id SERIAL NOT NULL PRIMARY KEY,
    quiz_tally_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    CONSTRAINT answer_unique_constraint UNIQUE (quiz_tally_id, profile_id),
    CONSTRAINT fk_answer_quiz_tally FOREIGN KEY(quiz_tally_id) REFERENCES quiz_tally(id),
    CONSTRAINT fk_answer_profile FOREIGN KEY(profile_id) REFERENCES profile(id)
);

CREATE TABLE voter (
    id SERIAL PRIMARY KEY NOT NULL,
    pool_tally_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    CONSTRAINT voter_unique_constraint UNIQUE (pool_tally_id, profile_id),
    CONSTRAINT fk_voter_pool_tally FOREIGN KEY(pool_tally_id) REFERENCES pool_tally(id),
    CONSTRAINT fk_voter_profile FOREIGN KEY(profile_id) REFERENCES profile(id)
);
