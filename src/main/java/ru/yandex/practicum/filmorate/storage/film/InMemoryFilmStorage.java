package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate FILMOGRAPHY_START_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LEN = 200;
    private final Map<Long, Film> films = new HashMap<>();
    private static long filmId = 0;

    private static Long generatedIDFilms() {
        return ++filmId;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Film getFilm(Long filmId) {
        checkFilmId(filmId);
        log.info("запрошен фильм: {}", films.get(filmId).toString());
        return films.get(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        if (films.values().stream()
                .filter(x -> x.getName().equalsIgnoreCase(film.getName()))
                .anyMatch(x -> x.getReleaseDate().equals(film.getReleaseDate()))) {
            log.error("Фильм '{}' с датой релиза '{}' уже добавлен.",
                    film.getName(),
                    film.getReleaseDate()
            );
            throw new ValidationException("This film already exists");
        }
        isValid(film);
        film.setId(generatedIDFilms());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;

    }

    @Override
    public void deleteFilm(Long filmId) {
        checkFilmId(filmId);
        log.info("удаление фильма: {}", films.get(filmId).toString());
        films.remove(filmId);
    }


    @Override
    public Film updateFilm(Film film) {
        isValid(film);
        checkFilmId(film.getId());
        films.put(film.getId(), film);
        log.info("Отредактирован фильм '{}'", film.getName());
        return film;
    }

    private void isValid(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("invalid film name");
        } else if (film.getDescription().length() > MAX_DESCRIPTION_LEN || film.getDescription().isBlank()) {
            log.error(String.format("Максимальная длина описания — %s символов", MAX_DESCRIPTION_LEN));
            throw new ValidationException("invalid description");
        } else if (film.getReleaseDate().isBefore(FILMOGRAPHY_START_DATE)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("invalid release date");
        } else if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("invalid duration");
        }
    }

    private void checkFilmId(Long filmId) {
        if (filmId == null || !films.containsKey(filmId)) {
            log.error("Фильм с id '{}' не найден в списке InMemoryFilmStorage!", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id '%d' не найден в InMemoryFilmStorage.", filmId));
        }
    }
}