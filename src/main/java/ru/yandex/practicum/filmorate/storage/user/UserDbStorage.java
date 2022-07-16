package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.validator.UserValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserValidator userValidator;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("validDbUser") UserValidator userValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.userValidator = userValidator;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> allUser = jdbcTemplate.query(sql, (rs, rowNum) -> uploadUser(rs));
        Map<Integer, User> mapUser = new HashMap<>();
        for (User user : allUser) {
            mapUser.put(user.getId(), user);
        }
        log.debug("Текущее количество пользователей: {}", mapUser.size());
        return mapUser;
    }

    @Override
    public User addUser(User user) {
        try {
            userValidator.validateUser(user);
            userValidator.isValidExistFilm(user);
            Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("USERS")
                    .usingColumns("name", "birthday", "login", "email")
                    .usingGeneratedKeyColumns("user_id")
                    .executeAndReturnKeyHolder(Map.of("name", user.getName(),
                            "birthday", user.getBirthday(),
                            "login", user.getLogin(),
                            "email", user.getEmail()))
                    .getKeys();
            user.setId((Integer) keys.get("user_id"));
            insertFriends(user);
            return user;
        }catch (RuntimeException ex){
            throw new ValidationException(ex.getMessage() + "./n Фильм не добавлен");
        }
    }

    @Override
    public User getUser(Integer userId) {
        userValidator.validateIdUser(userId);
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);
        if (sqlRow.next()) {
            log.info("Пользователь найден: {} {}",
                    sqlRow.getInt("user_id"), sqlRow.getString("login"));
            User user = new User(
                    sqlRow.getString("name"),
                    sqlRow.getString("login"),
                    sqlRow.getString("email"),
                    Objects.requireNonNull(sqlRow.getDate("birthday")).toLocalDate());
            user.setId(sqlRow.getInt("user_id"));
            HashSet<Integer> friends = uploadFriends(userId, true);
            HashSet<Integer> follows = uploadFriends(userId, false);
            friends.addAll(follows);
            user.setIdFriendsList(friends);
            log.info("Пользователь найден: {} {}",user,user.getIdFriendsList());
            return user;
        } else {
            throw new UserNotFoundException("Нет такого пользователя");
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        userValidator.validateIdUser(userId);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public User updateUser(User user) {
        userValidator.validateUser(user);
        userValidator.validateIdUser(user.getId());
        String updateUsers = "UPDATE users SET name = ?, login = ?, email = ?,   birthday = ?" +
                " WHERE user_id = ?";
        String removeFriend = "DELETE FROM USERS_FRIENDS WHERE user_id = ? AND friend_id = ?";
        HashSet<Integer> onRemove = uploadFriends(user.getId(), true);
        onRemove.addAll(uploadFriends(user.getId(), false));
        HashSet<Integer> friends = user.getIdFriendsList();
        onRemove.removeAll(friends);
        for (Integer deleteFriend : onRemove) {
            jdbcTemplate.update(removeFriend, user.getId(), deleteFriend);
        }
        insertFriends(user);
        jdbcTemplate.update(updateUsers,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void addFriends(int userId, int friendsId) {
        userValidator.validateIdUser(userId);
        userValidator.validateIdUser(friendsId);
        User user = getUser(userId);
        HashSet<Integer> friendsUser = user.getIdFriendsList();
        friendsUser.add(friendsId);
        updateUser(user);
    }

    @Override
    public void deleteFriends(int userId, int friendsId) {
        userValidator.validateIdUser(userId);
        userValidator.validateIdUser(friendsId);
        User user = getUser(userId);
        user.getIdFriendsList().remove(friendsId);
        updateUser(user);
        updateUser(getUser(friendsId));
    }

    @Override
    public List<User> findAllFriends(Integer userId) {
        userValidator.validateIdUser(userId);
        User user = getUser(userId);
        HashSet<Integer> allFriend = user.getIdFriendsList();
        List<User> friends = new ArrayList<>();
        if (!allFriend.isEmpty()) {
            for (Integer id : allFriend) {
                friends.add(getUser(id));
            }
        }
        return friends;
    }

    @Override
    public List<User> findCommonFriends(int userId, int otherId) {
        userValidator.validateIdUser(userId);
        userValidator.validateIdUser(otherId);
        List<User> commonFriends = new ArrayList<>();
        User user = getUser(userId);
        User otherUser = getUser(otherId);
        for (Integer friend : user.getIdFriendsList()) {
            if (otherUser.getIdFriendsList().contains(friend)) {
                commonFriends.add(getUser(friend));
            }
        }
        return commonFriends;
    }

    private User uploadUser(ResultSet result) throws SQLException {
        Integer id = result.getInt("user_id");
        String email = result.getString("email");
        String name = result.getString("name");
        String login = result.getString("login");
        LocalDate birthday = result.getDate("birthday").toLocalDate();
        User user = new User(name, login, email, birthday);
        user.setId(id);
        HashSet<Integer> friend = uploadFriends(id, true);
        friend.addAll(uploadFriends(id, false));
        user.setIdFriendsList(friend);
        return user;
    }

    private HashSet<Integer> uploadFriends(Integer userId, boolean status) {
        String sql = "SELECT friend_id FROM USERS_FRIENDS " +
                "WHERE user_id = ? AND STATUS_FRIENDSHIP = ?";
        return new HashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId, status));
    }

    private void insertFriends(User user) {
        if (!user.getIdFriendsList().isEmpty()) {
            HashSet<Integer> friends = user.getIdFriendsList();
            for (Integer friend : friends) {
                String sql = "INSERT INTO USERS_FRIENDS (user_id, friend_id, STATUS_FRIENDSHIP) VALUES (?, ?, ?)";
                if (getUser(friend).getIdFriendsList().contains(user.getId())) {
                    jdbcTemplate.update(sql, user.getId(), friend, true);
                } else {
                    jdbcTemplate.update(sql, user.getId(), friend, false);
                }
            }
        }
    }
}