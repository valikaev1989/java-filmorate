package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.validator.UserValidator;
import ru.yandex.practicum.filmorate.storage.validator.ValidUser;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final UserValidator userValidator = new ValidUser();
    private static final Map<Integer, User> users = new HashMap<>();
    private static int userId = 0;

    private static Integer createID() {
        return ++userId;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        if (users.values().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(user.getLogin()))
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Пользователь '{}' с электронной почтой '{}' уже существует.",
                    user.getLogin(), user.getEmail());
            throw new ValidationException("This user already exists");
        }
        userValidator.validateUser(user);
        user.setId(createID());
        users.put(user.getId(), user);
        log.info("Добавлен User: {}", user);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        userValidator.validateIdUser(userId);
        log.info("удаление фильма: {}", users.get(userId).toString());
        users.remove(userId);
    }

    @Override
    public User updateUser(User user) {
        userValidator.validateUser(user);
        userValidator.validateIdUser(user.getId());
        users.put(user.getId(), user);
        log.info("Данные пользователя '{}' обновлены", user.getLogin());
        return user;
    }


    @Override
    public User getUser(Integer userId) {
        userValidator.validateIdUser(userId);
        return users.get(userId);
    }

    @Override
    public void addFriends(int userId, int friendId) {
        userValidator.validateIdUser(userId);
        userValidator.validateIdUser(friendId);
        User user = users.get(userId);
        User friend = users.get(friendId);
        HashSet<Integer> userFriends = user.getIdFriendsList();
        HashSet<Integer> friendFriends = friend.getIdFriendsList();
        userFriends.add(friendId);
        friendFriends.add(userId);
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        userValidator.validateIdUser(userId);
        userValidator.validateIdUser(friendId);
        users.get(userId).getIdFriendsList().remove(friendId);
        users.get(friendId).getIdFriendsList().remove(userId);
    }

    @Override
    public List<User> findAllFriends(Integer userId) {
        userValidator.validateIdUser(userId);
        return users.get(userId).getIdFriendsList().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(int userId, int otherId) {
        User user = users.get(userId);
        User otherUser = users.get(otherId);
        Set<Integer> userFriends = user.getIdFriendsList();
        Set<Integer> otherUserFriends = otherUser.getIdFriendsList();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }
}