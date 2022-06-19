package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long id;
    private Set<Long> idLikeFilm;

    public Film(String name, String description, LocalDate releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}