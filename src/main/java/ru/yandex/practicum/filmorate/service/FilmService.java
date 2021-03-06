package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.validator.FilmValidator;
import ru.yandex.practicum.filmorate.storage.validator.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmValidator filmValidator;
    private final UserValidator userValidator;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
                       @Qualifier("ValidDbFilm") FilmValidator filmValidator, @Qualifier("ValidDbUser") UserValidator userValidator) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getFilmsByCountLikes(Integer count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAllFilms().values());
        return allFilms.stream()
                .sorted((o1, o2) -> {
                    int result = Integer.compare(o1.getIdLikeFilm().size(), o2.getIdLikeFilm().size());
                    return result * -1;
                }).limit(count)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        Film film1 = filmStorage.addFilm(film);
        log.info("addFilm: {}", film1);
        return film1;
    }

    public Film getFilm(Integer filmId) {
        Film film = filmStorage.getFilm(filmId);
        log.info("getFilm: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film film1 = filmStorage.updateFilm(film);
        log.info("updateFilm: {}", film1);
        return film1;
    }

    public void deleteFilm(Integer filmId) {
        filmStorage.deleteFilm(filmId);
        log.info("deleteFilm: {}", filmId);
    }

    public Film addLike(Integer filmId, Integer userId) {
        isValidId(filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        User user = userService.getUser(userId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
        log.info("User: {} was like film: {}", user, film);
        return film;
    }

    public void deleteLike(Integer filmId, Integer userId) {
        isValidId(filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        User user = userService.getUser(userId);
        checkUserLike(film, userId);
        film.getIdLikeFilm().remove(userId);
        filmStorage.updateFilm(film);
        log.info("User: {} was delete like film: {}", user, film);
    }

    private void isValidId(Integer filmId, Integer userId) {
        filmValidator.validateFilmId(filmId);
        userValidator.validateIdUser(userId);
    }

    private void checkUserLike(Film film, Integer userId) {
        if (!(film.getIdLikeFilm().contains(userId))) {
            log.error("???????????????????????? ?? id '{}' ?????????????????????? ?? ???????????? Like ????????????.", userId);
            throw new FilmNotFoundException(
                    String.format("???????????????????????? ?? id '%d' ?????????????????????? ?? ???????????? Like ????????????.", userId)
            );
        }
    }
}