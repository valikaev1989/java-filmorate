package ru.yandex.practicum.filmorate.models.Film;

import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private HashSet<Long> idLikeFilm = new HashSet<>();
    private HashSet<Integer> genreId = new HashSet<>();
    private Integer ratingId;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Integer genreId, Integer ratingId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        setGenreId(genreId);
        this.ratingId = ratingId;
    }

    public void setGenreId(Integer genre) {
        if (genre < 7 & genre > 0) {
            this.genreId.add(genre);
        } else {
            throw new ValidationException("Некорректное id: " + genre + " . Такого жанра нет");
        }
    }
}