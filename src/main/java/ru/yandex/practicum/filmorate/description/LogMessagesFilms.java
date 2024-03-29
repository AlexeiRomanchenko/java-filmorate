package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogMessagesFilms {
    USER_ALREADY_ADD_LIKE("Этот пользователь уже ставил лайк к этому фильму."),
    GET_ALL_FILMS_REQUEST("Получен запрос на получение всех фильмов."),
    GET_LIST_POPULAR_SORTED_FILMS_REQUEST("Получен запрос на получение списка популярных фильмов по количеству лайков " +
            "и отсортированных по жанру и/или году создания"),
    USER_SET_LIKE_FILM_REQUEST("Получен запрос на установку лайка."),
    USER_DELETE_LIKE_FILM_REQUEST("Получен запрос на удаление лайка."),
    FILMS_ID(" FilmID = "),
    GET_FILM_BY_ID_REQUEST("Получен запрос на получение фильма с ID = {}"),
    CREATE_FILM_REQUEST("Получен запрос на создание фильма."),
    UPDATE_FILM_REQUEST("Получен запрос на обновление данных фильма."),
    DELETE_FILM_REQUEST("Получен запрос на удаление фильма с ID = {}"),
    VALIDATION_FAILED("Ошибка валидации."),
    FILM_ADD("Добавлен фильм: "),
    DELETE_FILM("Фильм удален из базы."),
    FILM_NOT_VALIDATED_DATE("Данный фильм не прошел валидацию по дате: "),
    FILM_DATA_UPDATED("Данные фильма обновлены: "),
    FILM_NO_FOUND_WITH_ID("Нет фильма с id = "),
    GET_COMMON_FILMS_FOR_USERS_WITH_ID("Получение общих фильмов для пользователей с id = {}"),
    FILM_ALREADY_EXISTS("Данный фильм уже присутствует в базе: "),
    SEARCH_FOR_FILM("Получен запрос на поиск фильма содержащем: \"%s\" в параметрах: \"%s\"");

    private final String message;

}