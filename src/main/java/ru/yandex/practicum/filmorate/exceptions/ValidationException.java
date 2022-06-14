package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Throwable {
    public ValidationException(String s) {
        super(s);
    }
}