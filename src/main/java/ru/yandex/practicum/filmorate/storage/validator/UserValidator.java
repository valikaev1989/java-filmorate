package ru.yandex.practicum.filmorate.storage.validator;

import ru.yandex.practicum.filmorate.models.User;

public interface UserValidator {

    void validateIdUser(Integer userId);

    void validateUser(User user);
}
