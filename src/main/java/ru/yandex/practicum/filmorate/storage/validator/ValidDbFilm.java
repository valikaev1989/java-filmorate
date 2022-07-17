package ru.yandex.practicum.filmorate.storage.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

@Component
@Slf4j
@Qualifier("ValidDbFilm")
public class ValidDbFilm extends ValidFilm implements FilmValidator {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ValidDbFilm(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void validateFilm(Film film) {
        isValidNameFilm(film);
        isValidDescFilm(film);
        isValidDateFilm(film);
        isValidDurFilm(film);
    }

    @Override
    public void validateFilmId(Integer filmId) {
        if (filmId < 0) {
            throw new FilmNotFoundException(" filmId меньше нуля:" + filmId);
        }
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", filmId);
        if (!sqlRow.next()) {
            throw new ValidationException("Нет такого фильма с filmId:" + filmId);
        }
    }

    @Override
    public void isValidExistFilm(Film film) {
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM films WHERE name = ? AND release_date = ?",
                film.getName(), film.getReleaseDate()
        );
        if (sqlRow.next()) {
            throw new ValidationException("Этот фильм уже существует");
        }
    }
}