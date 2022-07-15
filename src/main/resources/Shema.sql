DROP TABLE IF EXISTS film_genres,genres,film_likes,film_ratings,films,friendship,users;

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name     VARCHAR(50)                          NOT NULL
);

CREATE TABLE IF NOT EXISTS film_ratings
(
    rating_id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name      VARCHAR(50)                          NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name           VARCHAR(255),
    description    VARCHAR(255),
    release_date   DATE,
    duration       INT,
    film_rating_id INTEGER,
    CONSTRAINT fk_film_ratings FOREIGN KEY (film_rating_id) REFERENCES film_ratings (rating_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id       BIGINT NOT NULL,
    film_genre_id INTEGER,
    CONSTRAINT film_genres_PK PRIMARY KEY (film_id, film_genre_id),
    CONSTRAINT film_genres_FK_1 FOREIGN KEY (film_id) REFERENCES films (film_id),
    CONSTRAINT film_genres_FK_2 FOREIGN KEY (film_genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(30) NOT NULL,
    login    VARCHAR(30) NOT NULL,
    name     VARCHAR(30),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friendship
(

    friend_id         BIGINT NOT NULL,
    user_id           BIGINT NOT NULL,
    friendship_status BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_friends PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friends1 FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_friends2 FOREIGN KEY (friend_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    user_id BIGINT,
    film_id BIGINT,
    CONSTRAINT LIKES_PK PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_films FOREIGN KEY (film_id) REFERENCES films (film_id),
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (user_id)
);