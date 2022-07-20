package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("dev")
class FilmAndUserDBApplicationTests {
    private final UserService userStorage;
    private final FilmService filmStorage;

    @Test
    public void testAddFilmAndUser() {
        User user = new User("user1", "u1", "u1@yandex.ru", LocalDate.of(2000, 5, 20));
        User user2 = new User("", "u2", "u2@yandex.ru", LocalDate.of(1995, 5, 20));
        User user3 = new User("user3", "u3", "u3@yandex.ru", LocalDate.of(2005, 5, 20));
        User user4 = new User("user4", "u4", "u4@yandex.ru", LocalDate.of(1008, 5, 20));
        User user5 = new User("", "u5", "u5@yandex.ru", LocalDate.of(2010, 5, 20));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addUser(user4);
        userStorage.addUser(user5);
        assertTrue(userStorage.getAllUsers().containsKey(user.getId()));
        assertTrue(userStorage.getAllUsers().containsKey(user2.getId()));
        assertTrue(userStorage.getAllUsers().containsKey(user3.getId()));
        assertTrue(userStorage.getAllUsers().containsKey(user4.getId()));
        assertTrue(userStorage.getAllUsers().containsKey(user5.getId()));

        Film film = new Film("film1", "some things", LocalDate.of(1980, 6, 15), 165, new Mpa(2, null));
        HashSet<Genre> genres = new HashSet<>();
        genres.add(new Genre(3, null));
        genres.add(new Genre(6, null));
        genres.add(new Genre(2, null));
        film.setGenres(genres);
        filmStorage.addFilm(film);
        assertTrue(filmStorage.getAllFilms().containsKey(film.getId()));

        Film film2 = new Film("film1", "some things", LocalDate.of(2000, 6, 15), 165, new Mpa(4, null));
        HashSet<Genre> genres2 = new HashSet<>();
        genres2.add(new Genre(5, null));
        film.setGenres(genres2);
        filmStorage.addFilm(film2);

        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.addLike(film.getId(), user2.getId());
        filmStorage.addLike(film.getId(), user3.getId());
        filmStorage.addLike(film2.getId(), user5.getId());
        Film film3 = filmStorage.getFilm(film.getId());
        Film film4 = filmStorage.getFilm(film2.getId());
        assertEquals(3, film3.getIdLikeFilm().size());
        assertEquals(1, film4.getIdLikeFilm().size());
        assertEquals(film3, filmStorage.getFilmsByCountLikes(1).get(0));
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film("Fake", "some things", LocalDate.of(1999, 6, 15), 165, new Mpa(2, null));
        film.addGenre(new Genre(6, null));
        filmStorage.addFilm(film);
        System.out.println(filmStorage.getFilm(1));
        Film film2 = new Film("Fake", "some things", LocalDate.of(1999, 6, 15), 165, new Mpa(2, "PG"));
        film2.setId(1);
        film2.setGenres(filmStorage.getFilm(film.getId()).getGenres());
        filmStorage.updateFilm(film2);
        System.out.println(filmStorage.getFilm(1));
        System.out.println(film2);
        assertEquals(film2, filmStorage.getFilm(1));
    }

    @Test
    public void testCommonFriends() {
        User user = new User("user1", "u1", "u1@yandex.ru", LocalDate.of(2001, 5, 20));
        User user2 = new User("user2", "u2", "u2@yandex.ru", LocalDate.of(1995, 5, 20));
        User user3 = new User("user3", "u3", "u3@yandex.ru", LocalDate.of(2005, 5, 20));
        User user4 = new User("user4", "u4", "u4@yandex.ru", LocalDate.of(1008, 5, 20));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addUser(user4);
        for (User user1 : userStorage.getAllUsers().values()) {
            System.out.println(user1);
        }
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(2, 3);
        userStorage.addFriend(1, 4);
        for (User user1 : userStorage.getAllUsers().values()) {
            System.out.println(user1 + " " + user1.getIdFriendsList());
        }
        userStorage.getCommonFriends(1, 2).forEach(System.out::println);
    }

    @Test
    void testGetUserFriends() {
        User user = new User("user1", "u1", "u1@yandex.ru", LocalDate.of(2001, 5, 20));
        User user2 = new User("user2", "u2", "u2@yandex.ru", LocalDate.of(1995, 5, 20));
        User user3 = new User("user3", "u3", "u3@yandex.ru", LocalDate.of(2005, 5, 20));
        User user4 = new User("user4", "u4", "u4@yandex.ru", LocalDate.of(1008, 5, 20));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addUser(user4);

        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(1, 4);
        userStorage.getAllFriends(1).forEach(System.out::println);
        userStorage.deleteFriends(1, 3);
        userStorage.getAllFriends(1).forEach(System.out::println);
    }
}
