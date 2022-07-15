package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.userStorage = storage;
    }

    public Map<Integer, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        User user1 = userStorage.addUser(user);
        log.info("addUser: {}", user1);
        return user1;
    }

    public User getUser(Integer userId) {
        User user = userStorage.getUser(userId);
        log.info("getUser: {}", user);
        return user;
    }

    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
        log.info("deleteUser: {}", userId);
    }

    public User updateUser(User user) {
        User user1 = userStorage.updateUser(user);
        log.info("updateUser: {}", user1);
        return user1;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriends(userId, friendId);
        log.info("addFriend: {}" + userStorage.getUser(userId).getIdFriendsList().contains(friendId), friendId);
    }

    public void deleteFriends(int userId, int friendId) {
        userStorage.deleteFriends(userId, friendId);
        log.info("deleteFriends: {}" + !userStorage.getUser(userId).getIdFriendsList().contains(friendId), friendId);
    }

    public List<User> getAllFriends(Integer userId) {
        return userStorage.findAllFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.findCommonFriends(userId, otherId);
    }
}