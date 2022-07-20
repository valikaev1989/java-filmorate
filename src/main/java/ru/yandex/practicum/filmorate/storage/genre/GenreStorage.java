package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(Integer id);
}