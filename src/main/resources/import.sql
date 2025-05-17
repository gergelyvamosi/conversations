-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS conference_management;

-- Use the database
USE conference_management;

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the conferences table
CREATE TABLE IF NOT EXISTS conferences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    planned_date DATE NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Create the conference_users join table
CREATE TABLE IF NOT EXISTS conference_users (
    conference_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (conference_id) REFERENCES conferences(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (conference_id, user_id)
);

-- Create the conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_timestamp DATETIME NOT NULL,
    user_a_id INT NOT NULL,
    user_b_id INT NOT NULL,
    text TEXT NOT NULL,
    archived CHAR(1) NOT NULL,
    conference_id INT NOT NULL,
    FOREIGN KEY (user_a_id) REFERENCES users(id),
    FOREIGN KEY (user_b_id) REFERENCES users(id),
    FOREIGN KEY (conference_id) REFERENCES conferences(id)
);

-- Insert initial data into the users table
INSERT INTO users (name) VALUES ('john'), ('greg');

-- Insert initial data into the conferences table
INSERT INTO conferences (title, planned_date, description, location)
VALUES ('Tech Conference 2024', '2024-12-31', 'The ultimate tech conference', 'New York');
INSERT INTO conferences (title, planned_date, description, location)
VALUES ('AI Workshop 2025', '2025-01-15', 'Hands-on AI workshop', 'San Francisco');

-- Insert initial data into the conference_users table
INSERT INTO conference_users (conference_id, user_id) VALUES (1, 1), (1, 2);

-- Insert initial data into the conversations table
INSERT INTO conversations (created_timestamp, user_a_id, user_b_id, text, archived, conference_id)
VALUES (NOW(), 1, 2, 'Hello Jane!', 'N', 1);
INSERT INTO conversations (created_timestamp, user_a_id, user_b_id, text, archived, conference_id)
VALUES (NOW(), 2, 1, 'Hi John!', 'N', 1);