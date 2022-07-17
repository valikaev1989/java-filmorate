package ru.yandex.practicum.filmorate.storage.validator;

import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.util.Map;

public interface UserValidator {
    void validateIdUser(Integer userId);

    void validateUser(User user);

    void isValidExistFilm(User user);

    void setMapFilms(Map<Integer, User> allUsers);
}