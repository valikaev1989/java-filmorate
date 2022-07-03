package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film.Film;

import java.util.Map;

public interface FilmStorage {
    Film addFilm(Film film);

    void deleteFilm(Long filmId);

    Film updateFilm(Film film);

    Map<Long, Film> getAllFilms();

    Film getFilm(Long filmId);
}