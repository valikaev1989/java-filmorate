package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Data
public class Genre {
    private int id;
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
//    public Genre(int id) {
//        this.id = id;
//
//        switch (id) {
//            case 1:
//                name = "Комедия";
//                break;
//            case 2:
//                name = "Драма";
//                break;
//            case 3:
//                name = "Мультфильм";
//                break;
//            case 4:
//                name = "Триллер";
//                break;
//            case 5:
//                name = "Документальный";
//                break;
//            case 6:
//                name = "Боевик";
//                break;
//            default:
//                throw new ValidationException("Такого Id нет");
//        }
//    }
}