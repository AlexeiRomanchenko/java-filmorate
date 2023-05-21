package ru.yandex.practicum.filmorate.description;

public enum LogMessagesUsers {
    GET_ALL_USERS_REQUEST("Получен запрос на получение всех пользователей."),
    USER_ADD("Добавлен пользователь: "),
    VALIDATION_FAILED("Ошибка валидации"),
    USER_DATA_UPDATED("Данные пользователя обновлены: "),
    USER_NO_FOUND("Пользователь не найден: "),
    USER_ALREADY_EXISTS("Данный пользователь уже присутствует в базе: ");

    private final String message;

    LogMessagesUsers(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}