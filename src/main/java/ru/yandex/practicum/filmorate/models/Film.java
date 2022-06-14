package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
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

    private String description;

    private LocalDate releaseDate;
}