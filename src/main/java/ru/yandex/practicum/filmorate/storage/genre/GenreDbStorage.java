package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    private static final String SELECT_ALL = "SELECT * FROM GENRES";
    private static final String SELECT_ById = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL, (this::uploadGenre));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_ById, id);
        if (rowSet.next()) {
            return Optional.of(new Genre(rowSet.getInt("genre_id"),
                    rowSet.getString("genre_name")));
        } else {
            throw new ValidationException("Жанра в базе с id " + id + "нет");
        }
    }

    private Genre uploadGenre(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() != 0) {
            return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
        } else {
            throw new ValidationException("Нет жанра в базе");
        }
    }
}