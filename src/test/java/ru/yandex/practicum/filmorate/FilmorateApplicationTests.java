package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {
	private static Validator validator;

	@BeforeAll
	static void BeforeAll() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldThrowIfUserLoginIsEmpty() {
		User user = new User();
		user.setName("name");
		user.setEmail("test@ya.ru");
		user.setBirthday(now().minusYears(20L));
		var message = validator.validate(user).iterator().next().getMessage();
		assertEquals("login can't be empty", message);
	}

	@Test
	void shouldThrowIfUserEmailIsInvalid() {
		User user = new User();
		user.setName("name");
		user.setLogin("login");
		user.setEmail("te@st@ya.ru");
		user.setBirthday(now().minusYears(20L));
		var message = validator.validate(user).iterator().next().getMessage();
		assertEquals("format Email incorrect", message);
	}

	@Test
	void shouldThrowIfUserBirthdayIsInvalid() {
		User user = new User();
		user.setName("name");
		user.setLogin("login");
		user.setEmail("test@ya.ru");
		user.setBirthday(now().plusDays(1));
		var message = validator.validate(user).iterator().next().getMessage();
		assertEquals("Invalid past date birthday.", message);
	}

	@Test
	void shouldThrowIfFilmNameIsEmpty() {
		Film film = new Film();
		film.setDescription("description");
		film.setReleaseDate(now().minusYears(20L));
		film.setDuration(60L);
		var message = validator.validate(film).iterator().next().getMessage();
		assertEquals("name can't be empty", message);
	}

	@Test
	void shouldThrowIfFilmDurationIsZero() {
		Film film = new Film();
		film.setName("Interstellar");
		film.setDescription("no time for caution");
		film.setReleaseDate(LocalDate.of(2014, 4, 26));
		film.setDuration(0L);
		var message = validator.validate(film).iterator().next().getMessage();
		assertEquals("duration should be positive", message);
	}

	@Test
	void shouldThrowIfFilmDurationIsNegative() {
		Film film = new Film();
		film.setName("Interstellar");
		film.setDescription("no time for caution");
		film.setReleaseDate(LocalDate.of(2014, 4, 26));
		film.setDuration(-1L);
		var message = validator.validate(film).iterator().next().getMessage();
		assertEquals("duration should be positive", message);
	}
}
