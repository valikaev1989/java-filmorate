package ru.yandex.practicum.filmorate.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbController {
    @Autowired
    private UserService userStorage;

    @Test
    void getAllUsers() {
        assertFalse(userStorage.getAllUsers().isEmpty());
    }
    @AfterEach
    void clear() {
        userStorage = null;
    }
    User user = new User(
            "user",
            "user_1",
            "user1@qwe.ru",
            LocalDate.of(2000, 1, 1)
    );
    User userChange = new User(
            "user2",
            "user_2",
            "user2@qwe.ru",
            LocalDate.of(2000, 2, 2)
    );
    User userFuture = new User(
            "user3",
            "user_3",
            "user3@qwe.ru",
            LocalDate.of(2222, 3, 3)
    );

    @Test
    void addUser() {

        assertThrows(ValidationException.class, () -> userStorage.addUser(userFuture));
        assertNotNull(userStorage.addUser(user).getId());
    }

    @Test
    void deleteUser() {
        userStorage.deleteUser(userStorage.addUser(user).getId());
        assertFalse(userStorage.getAllUsers().containsValue(user));
    }

    @Test
    void changeUser() {
        assertTrue(userStorage.addUser(user).getName().equals("user"));
        assertTrue(userStorage.getAllUsers().size() == 1);
        userChange.setId(1);
        assertTrue(userStorage.updateUser(userChange).getName().equals("user2"));
        assertTrue(userStorage.getAllUsers().size() == 1);
    }

    @Test
    void findUserById() {
        for (User u:userStorage.getAllUsers().values()){
            System.out.println(u);
        }
        User user1 = userStorage.addUser(user);
        assertEquals(user1, userStorage.getUser(user1.getId()));
    }
}
