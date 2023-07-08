package ru.yandex.practicum.filmorate.description;

public enum LogMessagesUsers {
    CREATE_USER("Создание пользователя"),
    FRIEND_ALREADY_ADD("Друг уже был добавлен ранее."),
    ID_NOT_POSITIVE("Id должен быть положительным."),
    GET_ALL_USERS_REQUEST("Получен запрос на получение всех пользователей."),
    CREATE_USER_REQUEST("Получен запрос на создание пользователя."),
    USER_ADD_FRIEND_REQUEST("Получен запрос на добавление друга."),
    USER_DELETE_FRIEND_REQUEST("Получен запрос на удаление друга из списка друзей."),
    GET_LIST_FRIENDS_USER_REQUEST("Получен запрос на получение списка друзей пользователя."),
    GET_LIST_COMMON_FRIENDS_REQUEST("Получен запрос на получение списка общих друзей пользователей."),
    USER_ID(" User ID = "),
    FRIEND_ID(" Frend ID = "),
    UPDATE_USER_REQUEST("Получен запрос на обновление данных пользователя."),
    GET_USER_BY_ID_REQUEST("Получен запрос на получение пользователя с ID = "),
    USER_ADD("Добавлен пользователь: "),
    DELETE_USER("Пользователь идален из базы"),
    VALIDATION_FAILED("Ошибка валидации."),
    USER_DATA_UPDATED("Данные пользователя обновлены: "),
    USER_NO_FOUND_WITH_ID("Пользователя с таким Id не существует."),
    USER_ALREADY_EXISTS("Данный пользователь уже присутствует в базе: "),
    TRANSFER_LIST_ALL_USERS("Передан список всех пользователей");

    private final String message;

    LogMessagesUsers(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}