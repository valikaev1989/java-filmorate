package ru.yandex.practicum.filmorate.storage.validator;

import ru.yandex.practicum.filmorate.models.Film;

public interface FilmValidator {
    void validateFilm(Film film);

    void validateFilmId(Integer filmId);

    void isValidExistFilm(Film film);
}
