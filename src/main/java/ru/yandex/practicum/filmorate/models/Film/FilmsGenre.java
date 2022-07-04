package ru.yandex.practicum.filmorate.models.Film;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;

public class FilmsGenre {
    private static final HashMap<Integer, String> genres = new HashMap<>();

    public FilmsGenre() {
        genres.put(1, "Comedy");
        genres.put(2, "Drama");
        genres.put(3, "Cartoon");
        genres.put(4, "Thriller");
        genres.put(5, "Documentary");
        genres.put(6, "Action");
    }

    String getGenre(int genreId) {
        if (genres.containsKey(genreId)) {
            return genres.get(genreId);
        } else {
            throw new ValidationException("Такого жанра: " + genreId + " нет в базе");
        }
    }
}
