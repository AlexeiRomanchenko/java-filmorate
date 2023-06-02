package ru.yandex.practicum.filmorate.description;

public enum LogMessagesUsers {
    FRIEND_ALREDY_ADD("Друг уже был добавлен ранее."),
    ID_NOT_POSITIVE("Id должен быть положительным."),
    GET_ALL_USERS_REQUEST("Получен запрос на получение всех пользователей."),
    USER_ADD("Добавлен пользователь: "),
    VALIDATION_FAILED("Ошибка валидации"),
    USER_DATA_UPDATED("Данные пользователя обновлены: "),
    USER_NO_FOUND_WITH_ID("Пользователя с таким Id не существует."),
    USER_ALREADY_EXISTS("Данный пользователь уже присутствует в базе: ");

    private final String message;

    LogMessagesUsers(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}