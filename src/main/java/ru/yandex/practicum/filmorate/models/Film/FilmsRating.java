package ru.yandex.practicum.filmorate.models.Film;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;

class FilmsRating {
    private static final HashMap<Integer, String> ratings = new HashMap<>();

    public FilmsRating() {
        ratings.put(1, "G");
        ratings.put(2, "PG");
        ratings.put(3, "PG-13");
        ratings.put(4, "R");
        ratings.put(5, "NC-17");
    }

    String getRating(int ratingId) {
        if (ratingId < 5 & ratingId > 0) {
            throw new ValidationException("Такого рейтинга: " + ratingId + " в базе нет");
        } else {
            return ratings.get(ratingId);
        }
    }
}
