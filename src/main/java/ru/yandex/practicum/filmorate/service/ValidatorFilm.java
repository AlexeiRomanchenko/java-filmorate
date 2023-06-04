package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class ValidatorFilm {
    private static final LocalDate localDate = LocalDate.of(1895, 12, 28);

    public static void validator(Film film) {

        if (film.getName().isBlank()) {
            throw new ValidationException(LogMessagesFilms.VALIDATION_FAILED.getMessage());
        }

        if (!film.getReleaseDate().isAfter(localDate)) {
            log.error(LogMessagesFilms.FILM_NOT_VALIDATED_DATE.getMessage() + film.toString());
            throw new ValidationException(LogMessagesFilms.FILM_NOT_VALIDATED_DATE.getMessage());
        }

    }

    public static void validationFailed(Film film) throws ValidationException {
        log.error(LogMessagesFilms.VALIDATION_FAILED.getMessage() + film.toString());
        throw new FilmAlreadyExistException(LogMessagesFilms.VALIDATION_FAILED.getMessage());

    }

}