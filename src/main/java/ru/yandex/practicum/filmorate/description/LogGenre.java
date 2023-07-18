package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogGenre {
    GET_GENRE_REQUEST("Получен запрос на получение жанра с ID = {}"),
    GET_ALL_GENRE_REQUEST("Получен запрос на получение всех жанров");

    private final String message;

}