package ru.yandex.practicum.filmorate.description;

public enum LogGenre {
    GET_GENRE_REQUEST("Получен запрос на получение жанра с ID= "),
    GET_ALL_GENRE_REQUEST("Получен запрос на получение всех жанров");

    private final String message;
    LogGenre (String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}