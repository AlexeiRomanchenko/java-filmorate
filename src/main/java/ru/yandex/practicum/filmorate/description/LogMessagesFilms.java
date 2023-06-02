package ru.yandex.practicum.filmorate.description;

public enum LogMessagesFilms {
    USER_ALREDY_ADD_LIKE("Этот пользователь уже ставил лайк к этому фильму."),
    GET_ALL_FILMS_REQUEST("Получен запрос на получение всех фильмов."),
    VALIDATION_FAILED("Ошибка валидации"),
    FILM_ADD("Добавлен фильм: "),
    FILM_NOT_VALIDATED_DATE("Данный фильм не прошел валидацию по дате: "),
    FILM_DATA_UPDATED("Данные фильма обновлены: "),
    FILM_NO_FOUND_WITH_ID("Фильма с таким id нет."),
    FILM_ALREADY_EXISTS("Данный фильм уже присутствует в базе: ");

    private final String message;

    LogMessagesFilms(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}