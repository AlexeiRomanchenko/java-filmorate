package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogError {
    UNEXPECTED_ERROR("Произошла непредвиденная ошибка."),
    OBJECT_NOT_FOUND("Объект не найден."),
    INCORRECT_REQUEST("Некорректный запрос.");
    private final String message;

}