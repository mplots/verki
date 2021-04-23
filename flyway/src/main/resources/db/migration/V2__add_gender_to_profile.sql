
CREATE TABLE gender (
    id BIGINT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

INSERT INTO gender (id, name) VALUES (1, 'male');
INSERT INTO gender (id, name) VALUES (2, 'female');
INSERT INTO gender (id, name) VALUES (3, 'unknown');

ALTER TABLE profile ADD COLUMN gender_id BIGINT DEFAULT 3;

ALTER TABLE profile
ADD CONSTRAINT fk_profile_gender
FOREIGN KEY(gender_id) REFERENCES gender(id);