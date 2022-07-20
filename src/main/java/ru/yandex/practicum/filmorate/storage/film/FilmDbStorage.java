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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_ALL_FILMS = "SELECT * FROM films";
    private static final String GET_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String UPDATE_FILMS = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            " duration = ?, film_rating_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_GENRE = "DELETE FROM FILMS_GENRES WHERE film_id = ?";
    private static final String INSERT_GENRE = "INSERT INTO FILMS_GENRES (film_id, film_genre_id) VALUES (?, ?)";
    private static final String DELETE_LIKES = "DELETE FROM FILMS_LIKES WHERE film_id = ?";
    private static final String INSERT_LIKES = "INSERT INTO FILMS_LIKES (user_id, film_id) VALUES (?, ?)";
    private static final String ADD_GENRE_FILM = "SELECT name, genre_id from genres g " +
            "LEFT JOIN FILMS_GENRES fg on fg.film_genre_id = g.genre_id " +
            "where film_id=?";
    private static final String ADD_LIKE_FILM = "SELECT * FROM FILMS_LIKES WHERE film_id = ?";
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
        List<Film> allFilms = new ArrayList<>(jdbcTemplate.query(SELECT_ALL_FILMS, this::uploadFilm));
        Map<Integer, Film> filmsMap = new HashMap<>();
        for (Film film : allFilms) {
            filmsMap.put(film.getId(), film);
        }
        return filmsMap;
    }

    @Override
    public Film addFilm(Film film) {
        filmValidator.validateFilm(film);
        filmValidator.isValidExistFilm(film);
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingColumns("name", "description",
                        "release_date", "duration", "film_rating_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", Date.valueOf(film.getReleaseDate()),
                        "duration", film.getDuration(),
                        "film_rating_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) Objects.requireNonNull(keys).get("film_id"));
        film.setMpa(mpaService.findMpaById(film.getMpa().getId()));
        updateGenres(film);
        updateLikes(film);
        log.info("addFilm: {}", film);
        return film;
    }

    @Override
    public Film getFilm(Integer filmId) {
        filmValidator.validateFilmId(filmId);
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(GET_FILM, filmId);
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
        filmValidator.validateFilm(film);
        filmValidator.validateFilmId(film.getId());
        jdbcTemplate.update(UPDATE_FILMS,
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

    }

    @Override
    public void deleteFilm(Integer filmId) {
        jdbcTemplate.update(DELETE_FILM, filmId);
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update(DELETE_GENRE, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(idGenre -> jdbcTemplate.update(INSERT_GENRE, film.getId(), idGenre));
        }
    }

    private void updateLikes(Film film) {
        jdbcTemplate.update(DELETE_LIKES, film.getId());
        film.getIdLikeFilm()
                .forEach(idUser -> jdbcTemplate.update(INSERT_LIKES, idUser, film.getId()));
    }

    private void addGenreForFilm(Film film) {
        HashSet<Genre> genres = new HashSet<>(jdbcTemplate.query(ADD_GENRE_FILM,
                (rs, num) -> new Genre(rs.getInt("genre_id"), rs.getString("name")),
                film.getId())
        );
        film.setGenres(genres);
    }

    private void addLikeForFilm(Film film) {
        List<Integer> idUser = jdbcTemplate.query(ADD_LIKE_FILM, ((rs, rowNum) -> {
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
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    mpaService.findMpaById(rs.getInt("FILM_RATING_ID"))
            );
            film.setId(rs.getInt("film_id"));
            addLikeForFilm(film);
            addGenreForFilm(film);
            return film;
        } else {
            throw new FilmNotFoundException("Нет MPA в базе");
        }
    }
}