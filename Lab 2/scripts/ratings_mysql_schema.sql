-- 1. Create the database
CREATE DATABASE movie_ratings;

-- 2. Use the database
USE movie_ratings;

CREATE TABLE ratings (
    user_id VARCHAR(10),  -- User ID as a string
    movie_id VARCHAR(10), -- Movie ID as a string
    rating INT CHECK (rating BETWEEN 1 AND 10), -- Rating (1-10)
    PRIMARY KEY (user_id, movie_id) -- Composite Primary Key
);

INSERT INTO ratings (user_id, movie_id, rating) VALUES
('U001', 'M001', 8),
('U002', 'M002', 6),
('U003', 'M003', 9),
('U004', 'M001', 7),
('U005', 'M002', 5),
('U006', 'M003', 10),
('U007', 'M004', 4),
('U008', 'M005', 3),
('U009', 'M006', 7),
('U010', 'M007', 6);

select * from ratings
