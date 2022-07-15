package ru.yandex.practicum.filmorate.storage.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Slf4j
@Component
@Qualifier("ValidUser")
public class ValidUser extends InMemoryUserStorage implements UserValidator {

    @Override
    public void validateIdUser(Integer userId) {
        if (userId == null || !getAllUsers().containsKey(userId)) {
            log.error("пользователь с id '{}' не найден в списке InMemoryUserStorage!", userId);
            throw new UserNotFoundException(String.format("пользователь с id '%d' не найден в InMemoryUserStorage.",
                    userId));
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

    }

    public void validateLoginUser(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не должен быть пустым и не должен содержать пробелов");
            throw new ValidationException("invalid login");
        }
    }

    public void validateBirthdayUser(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("invalid birthday");
        }
    }

    public void changeNameIfEmpty(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void validateEmailUser(User user) {
        final Pattern rfc2822 = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
                        "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
        );
        if (!rfc2822.matcher(user.getEmail()).matches()) {
            log.error("Некорректный адрес электронной почты");
            throw new ValidationException("Invalid email");
        }
    }
}