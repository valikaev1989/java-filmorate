package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.HashSet;
import java.util.Map;

public interface UserStorage {
    User addUser(User user) throws UserAlreadyExistException, InvalidEmailException;

    void deleteUser(Integer userId);

    User updateUser(User user);

    Map<Integer, User> getAllUsers();

    User getUser(Integer userId);

    void addFriends(int userId, int friendsId);

    void deleteFriends(int userId, int friendsId);

    HashSet<User> findAllFriends(Integer userId);

    HashSet<User> findCommonFriends(int userId, int otherId);
}