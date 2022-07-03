package ru.yandex.practicum.filmorate.models;

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
    private String name;
    private LocalDate birthday;
    private HashSet<Long> idFriendsList = new HashSet<>();
}