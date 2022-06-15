package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Validated
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private long idUsers = 0;

    private Long generatedIDUsers() {
        idUsers++;
        return idUsers;
    }

    private void createUser(User user) {
        user.setId(generatedIDUsers());
        users.put(user.getId(), user);
    }

    private static void validateUser(User user) {
        try {
            if (user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
                throw new ValidationException("error?как вывести это сообщение не знаю");
            }
            validateUser(user);
            createUser(user);
            log.debug(user.toString());
            return user;
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
                throw new ValidationException("error?как вывести это сообщение не знаю");
            }
            validateUser(user);
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.debug(user.toString());
                return user;
            } else {
                createUser(user);
                log.debug(user.toString());
                return user;
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
    }
}
