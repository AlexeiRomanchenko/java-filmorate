package ru.yandex.practicum.filmorate.description;

public enum LogMessagesFilms {
    GET_ALL_FILMS_REQUEST("Получен запрос на получение всех фильмов."),
    VALIDATION_FAILED("Ошибка валидации"),
    FILM_ADD("Добавлен фильм: "),
    FILM_NOT_VALIDATED_DATE("Данный фильм не прошел валидацию по дате: "),
    FILM_DATA_UPDATED("Данные фильма обновлены: "),
    FILM_NO_FOUND("Фильм не найден: "),
    FILM_ALREADY_EXISTS("Данный фильм уже присутствует в базе: ");

    private final String message;

    LogMessagesFilms(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}