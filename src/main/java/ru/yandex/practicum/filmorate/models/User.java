package ru.yandex.practicum.filmorate.models;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String name;
    private String login;
    private String email;
    private LocalDate birthday;
    private HashSet<Integer> idFriendsList = new HashSet<>();

    public User(String name, String login, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(Integer id, String name, String login, String email, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}