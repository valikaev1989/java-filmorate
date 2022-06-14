package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Validated
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private long idFilms = 0;
    private static final LocalDate FILMOGRAPHY_START_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LEN = 200;

    private Long generatedIDFilms() {
        idFilms++;
        return idFilms;
    }

    private void createFilm(Film film) {
        film.setId(generatedIDFilms());
        films.put(film.getId(), film);
    }

    private static void validateFilm(Film film) throws ValidationException {
        try {
            if (film.getReleaseDate().isBefore(FILMOGRAPHY_START_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше даты рождения кино");
            }
            if (film.getDescription().length() > MAX_DESCRIPTION_LEN) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        log.debug("Текущее количество пользователей: {}", films.size());
        return films.values();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) throws ValidationException {
        if (result.hasErrors()) {
            throw new ValidationException("error?как вывести это сообщение не знаю");
        }
        validateFilm(film);
        createFilm(film);
        log.debug(film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult result) throws ValidationException {
        try {
            if (result.hasErrors()) {
                throw new ValidationException("error?как вывести это сообщение не знаю");
            }
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.debug(film.toString());
                return film;
            } else {
                createFilm(film);
                log.debug(film.toString());
                return film;
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
    }
}
