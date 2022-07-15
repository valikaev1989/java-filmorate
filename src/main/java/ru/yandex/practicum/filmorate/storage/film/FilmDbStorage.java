package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.validator.FilmValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, @Qualifier("ValidDbFilm") FilmValidator filmValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.filmValidator = filmValidator;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> allFilms = new ArrayList<>(jdbcTemplate.query(sql, this::uploadFilm));
        Map<Integer, Film> filmsMap = new HashMap<>();
        for (Film film : allFilms) {
            filmsMap.put(film.getId(), film);
        }
        return filmsMap;
    }

    @Override
    public Film addFilm(Film film) {
        try {
            filmValidator.validateFilm(film);
            filmValidator.isValidExistFilm(film);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FILMS")
                    .usingGeneratedKeyColumns("FILM_ID");
            film.setId((Integer) simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)));
            updateGenres(film);
            updateLikes(film);
            log.info("addFilm: {}", film);
            return film;
        } catch (RuntimeException ex) {
            throw new ValidationException(ex.getMessage() + "./n Фильм не добавлен");
        }
    }

    @Override
    public Film getFilm(Integer filmId) {
        filmValidator.validateFilmId(filmId);
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(sql, filmId);
        if (sqlRow.next()) {
            Film film = new Film(
                    sqlRow.getString("name"),
                    sqlRow.getString("description"),
                    (Objects.requireNonNull(sqlRow.getDate("release_date"))).toLocalDate(),
                    sqlRow.getInt("duration"),
                    mpaService.findMpaById(sqlRow.getInt("film_rating_id"))
            );
            film.setId(sqlRow.getInt("film_id"));
            addGenreForFilm(film);
            addLikeForFilm(film);
            return film;
        } else {
            throw new FilmNotFoundException("Нет такого Film");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            filmValidator.validateFilm(film);
            filmValidator.validateFilmId(film.getId());
            String updateFilms = "UPDATE films SET name = ?, description = ?," +
                    " release_date = ?, duration = ?, film_rating_id = ?" +
                    " WHERE film_id = ?";
            jdbcTemplate.update(updateFilms,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            updateGenres(film);
            updateLikes(film);
            return getFilm(film.getId());
        } catch (RuntimeException ex) {
            throw new ValidationException(ex.getMessage() + "./n Фильм не был изменен");
        }
    }

    @Override
    public void deleteFilm(Integer filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        if (film.getMpa() != null) {
            values.put("FILM_RATING_ID", film.getMpa().getId());
        }
        return values;
    }

    private void updateGenres(Film film) {
        String deleteGenre = "DELETE FROM film_genres WHERE film_id = ?";
        String insertGenre = "INSERT INTO film_genres (film_id, film_genre_id) VALUES (?, ?)";
        jdbcTemplate.update(deleteGenre, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(idGenre -> jdbcTemplate.update(insertGenre, film.getId(), idGenre));
        }
    }

    private void updateLikes(Film film) {
        String deleteLikes = "DELETE FROM film_likes WHERE film_id = ?";
        String insertLikes = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(deleteLikes, film.getId());
        film.getIdLikeFilm()
                .forEach(idUser -> jdbcTemplate.update(insertLikes, idUser, film.getId()));
    }

    private void addGenreForFilm(Film film) {
        String sql = "SELECT name, genre_id from genres g " +
                "LEFT JOIN film_genres fg on fg.film_genre_id = g.genre_id " +
                "where film_id=?";
        HashSet<Genre> genres = new HashSet<>(jdbcTemplate.query(sql,
                (rs, num) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")),
                film.getId())
        );
        film.setGenres(genres);
    }

    private void addLikeForFilm(Film film) {
        String sqlLike = "SELECT * FROM film_likes WHERE film_id = ?";
        List<Integer> idUser = jdbcTemplate.query(sqlLike, ((rs, rowNum) -> {
            if (rs.getRow() != 0) {
                return rs.getInt("user_id");
            } else {
                throw new ValidationException("Нет LIKES");
            }
        }), film.getId());
        HashSet<Integer> likes = new HashSet<>(idUser);
        film.setIdLikeFilm(likes);
    }

    private Film uploadFilm(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() != 0) {
            Film film = new Film(rs.getString("film_name"),
                    rs.getString("film_description"),
                    rs.getDate("film_release_date").toLocalDate(),
                    rs.getInt("film_duration"),
                    mpaService.findMpaById(rs.getInt("film_rating"))
            );
            film.setId(rs.getInt("film_id"));
            addLikeForFilm(film);
            addGenreForFilm(film);
            return film;
        } else {
            throw new ValidationException("Нет MPA в базе");
        }
    }
}