package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.validator.FilmValidator;
import ru.yandex.practicum.filmorate.storage.validator.ValidFilm;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final FilmValidator filmValidator =new ValidFilm();
    private final Map<Integer, Film> films = new HashMap<>();
    private static int filmId = 0;

    private static Integer generatedIDFilms() {
        return ++filmId;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Film getFilm(Integer filmId) {
        filmValidator.setMapFilms(films);
        filmValidator.validateFilmId(filmId);
        log.info("запрошен фильм: {}", films.get(filmId).toString());
        return films.get(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        filmValidator.setMapFilms(films);
        filmValidator.validateFilm(film);
        film.setId(generatedIDFilms());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;

    }

    @Override
    public void deleteFilm(Integer filmId) {
        filmValidator.setMapFilms(films);
        filmValidator.validateFilmId(filmId);
        log.info("удаление фильма: {}", films.get(filmId).toString());
        films.remove(filmId);
    }


    @Override
    public Film updateFilm(Film film) {
        filmValidator.setMapFilms(films);
        filmValidator.validateFilm(film);
        filmValidator.validateFilmId(film.getId());
        films.put(film.getId(), film);
        log.info("Отредактирован фильм '{}'", film.getName());
        return film;
    }

    public List<Film> getFilmsByCountLikes(Integer count) {
        return getAllFilms().values().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getIdLikeFilm().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }
}