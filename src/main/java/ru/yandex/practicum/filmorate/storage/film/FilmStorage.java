package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    void deleteFilm(Integer filmId);

    Film updateFilm(Film film);

    Map<Integer, Film> getAllFilms();

    Film getFilm(Integer filmId);

}