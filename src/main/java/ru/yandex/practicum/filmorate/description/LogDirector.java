package ru.yandex.practicum.filmorate.description;

public enum LogDirector {
    GET_ALL_FILMS_BY_DIRECTOR_REQUEST("Запрос на получение списка фильмов режиссера "),
    SORTED_BY("Отсортированный по "),
    GET_LIST_ALL_DIRECTOR_REQUEST("Запрос на получение списка всех режиссеров"),
    GET_DIRECTOR_BY_ID_REQUEST("Запрос на получение режиссера с id = "),
    PUT_DIRECTOR_BY_ID_REQUEST("Запрос на обновление режиссера с id = "),
    DELETE_ALL_DIRECTORS_REQUEST("Запрос на удаление всех режиссеров"),
    DELETE_DIRECTOR_BY_ID_REQUEST("Запрос на удаление режиссера с id = "),
    POST_ADD_DIRECTOR("Запрос на добавления режиссера");

    private final String message;
    LogDirector(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}