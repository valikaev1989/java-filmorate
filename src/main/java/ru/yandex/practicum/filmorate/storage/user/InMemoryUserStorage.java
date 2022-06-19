package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static long userId = 0;

    private static long createID() {
        return ++userId;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public User addUser(User user) throws UserAlreadyExistException {
        User user1 = new User();
        if (users.values().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(user.getLogin()))
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Пользователь '{}' с электронной почтой '{}' уже существует.",
                    user.getLogin(), user.getEmail());
            throw new UserAlreadyExistException("This user already exists");
        }
        if (isValid(user)) {
            user.setId(createID());
            users.put(user.getId(), user);
            log.info("Добавлен User: {}", user);
            user1 = user;
        }
        return user1;
    }

    @Override
    public User deleteUser(Long userId) {
        User user = new User();
        if (checkId(userId)) {
            log.info("удаление фильма: {}", users.get(userId).toString());
            user = users.get(userId);
            users.remove(userId);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        User user1 = new User();
        if (isValid(user) && checkId(user.getId())) {
            users.put(user.getId(), user);
            log.info("Данные пользователя '{}' обновлены", user.getLogin());
            user1 = user;
        }
        return user1;
    }


    @Override
    public User getUser(Long userId) {
        User user = new User();
        if (checkId(userId)) {
            user = users.get(userId);
        }
        return user;
    }

    private boolean isValid(User user) throws ValidationException {
        if (checkEmail(user)) {
            log.error("Некорректный адрес электронной почты");
            throw new ValidationException("Invalid email");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не должен быть пустым и не должен содержать пробелов");
            throw new ValidationException("invalid login");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("invalid birthday");
        } else {
            if (user.getName().isBlank()) user.setName(user.getLogin());
            return true;
        }
    }

    private boolean checkId(Long userId) {
        if (userId == null || !users.containsKey(userId)) {
            log.error("пользователь с id '{}' не найден в списке InMemoryUserStorage!", userId);
            throw new UserNotFoundException(String.format("пользователь с id '%d' не найден в InMemoryUserStorage.",
                    userId));
        } else {
            return true;
        }
    }

    private boolean checkEmail(User user) {
        final Pattern rfc2822 = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
                        "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
        );
        return !rfc2822.matcher(user.getEmail()).matches();
    }
}