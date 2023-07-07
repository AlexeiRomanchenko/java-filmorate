DROP TABLE IF EXISTS review_useful;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS rating_mpa;

CREATE TABLE IF NOT EXISTS genres (
    genre_id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS rating_mpa (
    rating_id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    duration INT NOT NULL,
    release_date TIMESTAMP NOT NULL,
    rating_id INT REFERENCES rating_mpa(rating_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(255),
    login VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT NOT NULL REFERENCES films (film_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (genre_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INT NOT NULL REFERENCES films (film_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    user_id INT NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INT NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    friend_id INT NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     VARCHAR(255) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id     INT NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    film_id     INT NOT NULL REFERENCES films (film_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS review_useful (
    review_id   INT NOT NULL REFERENCES reviews (review_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    user_id     INT NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    grade       BOOLEAN NOT NULL
);