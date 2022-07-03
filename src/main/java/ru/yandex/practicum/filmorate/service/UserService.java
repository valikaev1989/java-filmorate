package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    Map<Long, User> users;


    @Autowired
    public UserService(UserStorage storage) {
        this.userStorage = storage;
        this.users = userStorage.getAllUsers();
    }

    public User addFriend(Long userId, Long friendId) {
        checkUserContainsInMap(userId);
        checkUserContainsInMap(friendId);
        User user = users.get(userId);
        User friend = users.get(friendId);
        HashSet<Long> userFriends = user.getIdFriendsList();
        HashSet<Long> friendFriends = friend.getIdFriendsList();
        userFriends.add(friendId);
        friendFriends.add(userId);
        return friend;
    }

    public void deleteFriend(Long userId, Long friendId) {
        checkUserContainsInMap(userId);
        checkUserContainsInMap(friendId);
        users.get(userId).getIdFriendsList().remove(friendId);
        users.get(friendId).getIdFriendsList().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        checkUserContainsInMap(userId);
        return users.get(userId).getIdFriendsList().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkUserContainsInMap(userId);
        checkUserContainsInMap(otherId);
        User user = users.get(userId);
        User otherUser = users.get(otherId);
        Set<Long> userFriends = user.getIdFriendsList();
        Set<Long> otherUserFriends = otherUser.getIdFriendsList();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(i -> users.get(i))
                .collect(Collectors.toList());
    }

    private void checkUserContainsInMap(Long userId) {
        if (!(users.containsKey(userId))) {
            log.error("Пользователь с id '{}' не найден в UserService.", userId);
            throw new UserNotFoundException(
                    String.format("Пользователь с id:'%d' не найден в UserService.", userId)
            );
        }
    }
}