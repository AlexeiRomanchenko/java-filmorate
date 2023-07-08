package ru.yandex.practicum.filmorate.description;

public enum LogDirector {
    GET_ALL_FILMS_BY_DIRECTOR_REQUEST("Запрос на получение списка фильмов режиссера "),
    SORTED_BY("Отсортированный по "),
    GET_LIST_ALL_DIRECTOR_REQUEST("Запрос на получение списка всех режиссеров"),
    GET_DIRECTOR_BY_ID_REQUEST("Запрос на получение режиссера с id = "),
    PUT_DIRECTOR_BY_ID_REQUEST("Запрос на обновление режиссера с id = "),
    DELETE_ALL_DIRECTORS_REQUEST("Запрос на удаление всех режиссеров"),
    DELETE_DIRECTOR_BY_ID_REQUEST("Запрос на удаление режиссера с id = "),
    NO_FOUND_DIRECTOR("Режиссер не найден."),
    DIRECTOR_ALREADY_EXISTS("Режиссер уже существует"),
    FAILED_TO_ADD_DIRECTOR("Не удалось добавить режиссера. "),
    FAILED_TO_REMOVE_DIRECTOR("Не удалось удалить режиссера. "),
    FAILED_TO_TRANSFER_DIRECTOR("Не удалось передать режиссера. "),
    FAILED_TO_UPDATE_DIRECTOR("Не удалось обновить режиссера. "),
    POST_ADD_DIRECTOR("Запрос на добавления режиссера"),
    UPDATE_DIRECTOR("Обновлен режиссер: "),
    ADD_DIRECTOR("Добавлен режиссер "),
    TRANSFER_DIRECTOR("Передан режиссер: "),
    TABLE_DIRECTOR_CLEAR("Таблица режиссеров очищена"),
    EMPTY_LIST_FILMS_DIRECTOR("Пустой список фильмов режиссера "),
    TRANSFER_SORTED_LIST("Передан отсортированный по"),
    LIST_FILMS_DIRECTOR("список фильмов режиссера."),
    DELETE_DIRECTOR("Удален режиссер");

    private final String message;

    LogDirector(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}