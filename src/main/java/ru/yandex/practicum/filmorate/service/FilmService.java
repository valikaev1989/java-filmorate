package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(Long filmId, Long userId) {
        Film film1 = new Film();
        if (isValidId(filmId, userId)) {
            Film film = filmStorage.getAllFilms().get(filmId);
            Set<Long> likes = film.getIdLikeFilm();
            likes.add(userId);
            film.setIdLikeFilm(likes);
            film1 = film;
        }
        return film1;
    }

    public void deleteLike(Long filmId, Long userId) {
        if (isValidId(filmId, userId)) {
            Film film = filmStorage.getAllFilms().get(filmId);
            if (!(film.getIdLikeFilm().contains(userId))) {
                log.error("Пользователь с id '{}' отсутствует в списке Like фильма.", userId);
                throw new FilmNotFoundException(
                        String.format("Пользователь с id '%d' отсутствует в списке Like фильма.", userId)
                );
            }
            film.getIdLikeFilm().remove(userId);
        }
    }

    public List<Film> getFilmsByCountLikes(Integer count) {
        return filmStorage.getAllFilms().values().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getIdLikeFilm().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isValidId(Long filmId, Long userId) {
        if (filmId == null || !filmStorage.getAllFilms().containsKey(filmId)) {
            log.error("Фильм с id '{}' не найден в списке FilmService!", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id '%d' не найден в FilmService.", filmId));
        } else if (userId == null || !userStorage.getAllUsers().containsKey(userId)) {
            log.error("пользователь с id '{}' не найден в списке FilmService!", userId);
            throw new UserNotFoundException(String.format("пользователь с id '%d' не найден в FilmService.", userId));
        } else {
            return true;
        }
    }
}