package ru.yandex.practicum.filmorate.models.User;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private HashSet<Long> idFriendsList = new HashSet<>();
    private int friendStatusId = 1;
}