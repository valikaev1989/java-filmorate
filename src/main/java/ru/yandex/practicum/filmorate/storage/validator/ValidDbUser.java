package ru.yandex.practicum.filmorate.storage.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

@Component
@Slf4j
@Qualifier("ValidDbUser")
public class ValidDbUser extends ValidUser implements UserValidator {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ValidDbUser(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void validateIdUser(Integer userId) {
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);
        if (!sqlRow.next()) {
            log.error("Нет такого пользователя с id:" + userId);
            throw new UserNotFoundException("Нет такого пользователя с id:" + userId);
        }
    }

    @Override
    public void validateUser(User user) {
        validateLoginUser(user);
        validateBirthdayUser(user);
        changeNameIfEmpty(user);
        validateEmailUser(user);
    }

    @Override
    public void isValidExistFilm(User user) {
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM USERS WHERE USER_ID = ? and name = ? AND EMAIL = ?",
                user.getId(), user.getName(), user.getEmail()
        );
        if (sqlRow.next()) {
            log.error("Этот пользователь уже существует" + user);
            throw new ValidationException("Этот пользователь уже существует");
        }
    }
}