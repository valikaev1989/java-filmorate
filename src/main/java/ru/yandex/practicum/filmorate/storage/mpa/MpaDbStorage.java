package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private static final String SELECT_ALL = "SELECT * FROM FILM_RATINGS";
    private static final String SELECT_BY_ID = "SELECT * FROM FILM_RATINGS WHERE rating_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(SELECT_ALL, this::uploadMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer id) {
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);
        if (sqlRow.next()) {
            Mpa mpa = new Mpa(
                    sqlRow.getInt("rating_id"),
                    sqlRow.getString("rating_name"));
            return Optional.of(mpa);
        } else {
            throw new ValidationException("Рейтинга фильма с id:" + id + "нет");
        }
    }

    private Mpa uploadMpa(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() != 0) {
            return new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"));
        } else {
            throw new ValidationException("Нет такого рейтинга фильма");
        }
    }
}