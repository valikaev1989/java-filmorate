MERGE INTO PUBLIC.GENRES (GENRE_ID, NAME)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');




MERGE INTO PUBLIC.FILM_RATINGS (RATING_ID, NAME)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG_13'),
       (4, 'R'),
       (5, 'NC_17');

