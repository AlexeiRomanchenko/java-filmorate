package ru.yandex.practicum.filmorate.exception;

public class DirectorAlreadyExistException extends RuntimeException {
    public DirectorAlreadyExistException(String message) {
        super(message);
    }

}