package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.models.User.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    @PostMapping()
    public User addUser(@RequestBody User user) throws ValidationException, UserAlreadyExistException, InvalidEmailException {
        return userStorage.addUser(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) throws ValidationException {
        return userStorage.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) throws ValidationException {
        userStorage.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userStorage.getUser(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}