package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.Map;

public interface UserStorage {
    User addUser(User user) throws UserAlreadyExistException, InvalidEmailException;

    void deleteUser(Long userId);

    User updateUser(User user);

    Map<Long, User> getAllUsers();

    User getUser(Long userId);
}