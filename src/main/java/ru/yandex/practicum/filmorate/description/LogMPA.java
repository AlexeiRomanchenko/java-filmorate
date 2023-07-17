package ru.yandex.practicum.filmorate.description;

public enum LogMPA {
    GET_ALL_MPA_REQUEST("Получен запрос на получение всех MPA"),
    GET_MPA_REQUEST("Получен запрос на получение MPA c ID = {}"),
    NO_FOUND_MPA("Рейтинг не найден");

    private final String message;

    LogMPA(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
