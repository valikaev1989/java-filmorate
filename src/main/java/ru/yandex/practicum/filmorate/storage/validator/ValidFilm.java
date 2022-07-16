package ru.yandex.practicum.filmorate.storage.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ValidFilm implements FilmValidator {
    private static final LocalDate FILMOGRAPHY_START_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LEN = 200;
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public void validateFilm(Film film) {
        isValidNameFilm(film);
        isValidDescFilm(film);
        isValidDateFilm(film);
        isValidDurFilm(film);
        isValidExistFilm(film);
    }

    @Override
    public void setMapFilms(Map<Integer, Film> allFilms) {
        this.films = allFilms;
    }

    @Override
    public void validateFilmId( Integer filmId) {
        if (filmId == null || !films.containsKey(filmId)) {
            log.error("Фильм с id '{}' не найден в списке InMemoryFilmStorage!", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id '%d' не найден в InMemoryFilmStorage.", filmId));
        }
    }

    public void isValidNameFilm(Film film) {
        if (film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("invalid film name");
        }
    }

    public void isValidDescFilm(Film film) {
        if (film.getDescription().length() > MAX_DESCRIPTION_LEN || film.getDescription().isBlank()) {
            log.error(String.format("Максимальная длина описания — %s символов", MAX_DESCRIPTION_LEN));
            throw new ValidationException("invalid description");
        }
    }

    public void isValidDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILMOGRAPHY_START_DATE)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("invalid release date");
        }
    }

    public void isValidDurFilm(Film film) {
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("invalid duration");
        }
    }

    @Override
    public void isValidExistFilm( Film film) {
        if (films.values().stream()
                .filter(x -> x.getName().equalsIgnoreCase(film.getName()))
                .anyMatch(x -> x.getReleaseDate().equals(film.getReleaseDate()))) {
            log.error("Фильм '{}' с датой релиза '{}' уже добавлен.",
                    film.getName(),
                    film.getReleaseDate()
            );
            throw new ValidationException("Этот фильм уже существует");
        }
    }
}