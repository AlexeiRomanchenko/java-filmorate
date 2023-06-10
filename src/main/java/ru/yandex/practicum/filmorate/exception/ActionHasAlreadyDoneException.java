package ru.yandex.practicum.filmorate.exception;

public class ActionHasAlreadyDoneException extends RuntimeException {
    public ActionHasAlreadyDoneException(String message) {
        super(message);
    }

}