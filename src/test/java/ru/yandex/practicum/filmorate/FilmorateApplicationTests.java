package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	FilmController filmController =new FilmController(FilmService);
	@Test
	void contextLoads() {
	}

	@Test
	void findAll(){
		Film film = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 03, 25), 100);
		filmController.create(film);
		assertEquals(1, filmController.findAll().size(), "Коллекция пуста");
	}

}