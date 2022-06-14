package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Positive(message = "id should be positive ")
    private Long id;

    @Email(message = "format Email incorrect")
    private String email;

    @NotEmpty(message = "login can't be empty")
    @NotBlank(message = "login can't be empty")
    private String login;

    private String name;

    @NonNull
    @Past(message = "Invalid past date birthday.")
    private LocalDate birthday;
}