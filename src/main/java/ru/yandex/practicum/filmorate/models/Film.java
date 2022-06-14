package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    @Positive(message = "id should be positive ")
    private Long id;

    @Positive(message = "duration should be positive")
    Long duration;

    @NotEmpty(message = "name can't be empty")
    @NotBlank(message = "name can't be empty")
    private String name;

    @NotNull(message = "description can't be null")
    private String description;

    @NotNull(message = "releaseDate can't be null")
    private LocalDate releaseDate;
}
