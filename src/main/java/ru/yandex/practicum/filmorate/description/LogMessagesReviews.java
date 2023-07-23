package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogMessagesReviews {
    MSG_ERR_ID("Некорректный id"),
    MSG_ERR_NOT_FOUND("Не найдено по ID = {}"),
    GET_ALL_REVIEWS_REQUEST("Получен запрос на получение всех отзывов."),
    GET_REVIEW_BY_ID_REQUEST("Получен запрос на получение отзыва с ID = {}"),
    CREATE_REVIEW_REQUEST("Получен запрос на создание отзыва."),
    UPDATE_REVIEW_REQUEST("Получен запрос на обновление данных отзыва."),
    DELETE_REVIEW_REQUEST("Получен запрос на удаление отзыва."),
    UPDATE_LIKE_REQUEST("Получен запрос на добавление лайка."),
    UPDATE_DISLIKE_REQUEST("Получен запрос на добавление дизлайка."),
    DELETE_LIKE_REQUEST("Получен запрос на удаление лайка."),
    DELETE_DISLIKE_REQUEST("Получен запрос на удаление дизлайка."),
    GET_REVIEWS_BY_FILM_REQUEST("Получен запрос на получение отзывов на фильм.");

    private final String message;

}
