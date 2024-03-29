package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogMessagesUsers {
    CREATE_USER("Создание пользователя"),
    FRIEND_ALREADY_ADD("Друг уже был добавлен ранее."),
    ID_NOT_POSITIVE("Id должен быть положительным."),
    GET_ALL_USERS_REQUEST("Получен запрос на получение всех пользователей."),
    CREATE_USER_REQUEST("Получен запрос на создание пользователя."),
    DELETE_USER_REQUEST("Получен запрос на удаление пользователя с ID = {}"),
    USER_ADD_FRIEND_REQUEST("Получен запрос на добавление друга."),
    USER_DELETE_FRIEND_REQUEST("Получен запрос на удаление друга из списка друзей."),
    GET_LIST_FRIENDS_USER_REQUEST("Получен запрос на получение списка друзей пользователя."),
    GET_LIST_COMMON_FRIENDS_REQUEST("Получен запрос на получение списка общих друзей пользователей."),
    GET_LIST_RECOMMENDED_FILMS_REQUEST("Получен запрос на получении списка рекомендованных фильмов для пользователя."),
    USER_ID(" User ID = "),
    FRIEND_ID(" Friend ID = "),
    UPDATE_USER_REQUEST("Получен запрос на обновление данных пользователя."),
    GET_USER_BY_ID_REQUEST("Получен запрос на получение пользователя с ID = {}"),
    GET_EVENTS_USER_BY_ID_REQUEST("Получен запрос на получение событий пользователя с ID = {}"),
    USER_ADD("Добавлен пользователь: "),
    DELETE_USER("Пользователь удален из базы"),
    VALIDATION_FAILED("Ошибка валидации."),
    USER_DATA_UPDATED("Данные пользователя обновлены: "),
    USER_NO_FOUND_WITH_ID("Пользователя с таким Id не существует."),
    USER_ALREADY_EXISTS("Данный пользователь уже присутствует в базе: "),
    NO_FOUND("не найден");

    private final String message;

}