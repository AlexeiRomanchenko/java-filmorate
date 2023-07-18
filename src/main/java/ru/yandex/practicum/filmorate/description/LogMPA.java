package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogMPA {
    GET_ALL_MPA_REQUEST("Получен запрос на получение всех MPA"),
    GET_MPA_REQUEST("Получен запрос на получение MPA c ID = {}"),
    NO_FOUND_MPA("Рейтинг не найден");

    private final String message;

}
