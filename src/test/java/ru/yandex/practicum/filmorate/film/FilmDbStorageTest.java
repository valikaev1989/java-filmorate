package ru.yandex.practicum.filmorate.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    @Qualifier("filmDbStorage")
    @Autowired
    private FilmStorage filmStorage;


    Film film = new Film(
            "фильм1",
            "опис1",
            LocalDate.of(2022, 1, 1),
            10,
            new Mpa(1, null)
    );
    Film filmNew = new Film(
            "фильм2",
            "опис2",
            LocalDate.of(2022, 2, 2),
            20,
            new Mpa(2, null)
    );

    Film filmPass = new Film(
            "фильм3",
            "опис3",
            LocalDate.of(1700, 10, 10),
            30,
            new Mpa(3, null)
    );
    Film filmChange = new Film(
            "фильм4",
            "опис4",
            LocalDate.of(2022, 4, 4),
            200,
            new Mpa(4, null)
    );
    Film filmDrop = new Film(
            "фильм5",
            "опис5",
            LocalDate.of(2022, 5, 5),
            200,
            new Mpa(5, null)
    );

    @Test
    void addFilm() {
        assertThrows(ValidationException.class, () -> filmStorage.addFilm(filmPass));
        assertNotNull(filmStorage.addFilm(film).getId());
    }

    @Test
    void deleteFilm() {
        for (Film f: filmStorage.getAllFilms().values()){
            System.out.println(f);
        }
        filmStorage.deleteFilm(film.getId());
        assertFalse(filmStorage.getAllFilms().containsValue(film));
    }

    @Test
    void changeFilm() {
        assertEquals("фильм1", filmStorage.addFilm(film).getName());
        filmChange.setId(1);
        assertEquals("фильм4", filmStorage.updateFilm(filmChange).getName());
    }

    @Test
    void getAllFilms() {
        filmStorage.addFilm(filmDrop);
        assertFalse(filmStorage.getAllFilms().isEmpty());
    }

    @Test
    void findFilmById() {

        Film film1 = filmStorage.addFilm(filmNew);
        assertEquals(filmStorage.getFilm(film1.getId()).getName(), film1.getName());
    }
}