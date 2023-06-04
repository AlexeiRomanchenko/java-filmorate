package ru.yandex.practicum.filmorate.description;

public enum LogError {
    UNEXPECTED_ERROR("Произошла непредвиденная ошибка."),
    OBJECT_NOT_FOUND("Объект не найден"),
    INCORRECT_REQUEST("Некорректный запрос");
    private final String message;

    LogError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
