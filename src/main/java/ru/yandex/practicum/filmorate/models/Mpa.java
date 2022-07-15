package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Data
public class Mpa {
    private int id;
    private String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Mpa(int id) {
        this.id = id;
        switch (id) {
            case 1:
                name = "G";
                break;
            case 2:
                name = "PG";
                break;
            case 3:
                name = "PG-13";
                break;
            case 4:
                name = "R";
                break;
            case 5:
                name = "NC-17";
                break;
            default:
                throw new ValidationException("Такого ID рейтинга нет");
        }
    }
}