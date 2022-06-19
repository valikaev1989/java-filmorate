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
    private Long id;
    private Long duration;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Long> idLikeFilm;
}