package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Validated
@RestController
@Slf4j
public class FilmController {
    @Future
    private static final LocalDate localDate = LocalDate.of(1895, 12, 28);
    private int nextId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        log.info(LogMessagesFilms.GET_ALL_FILMS_REQUEST.getMessage());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        if (validator(film)) {
            if (films.get(film.getId()) != null) {
                log.info(LogMessagesFilms.FILM_ALREADY_EXISTS.getMessage() + film.toString());
                throw new FilmAlreadyExistException();
            }
            film.setId(nextId);
            films.put(film.getId(), film);
            nextId++;
            log.info(LogMessagesFilms.FILM_ADD.getMessage() + film.toString());
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        checkDataRelease(film);
        if (films.get(film.getId()) != null) {
            films.put(film.getId(), film);
            log.info(LogMessagesFilms.FILM_DATA_UPDATED.getMessage() + film.toString());
        } else {
            validationFailed(film);
        }
        return film;
    }

    private boolean validator(@NonNull Film film) {
        checkDataRelease(film);

        if (film.getName().isBlank()) {
            throw new ValidationException(LogMessagesFilms.VALIDATION_FAILED.getMessage());
        }

        if (film.getDescription().length() >= 200) {
            throw new ValidationException(LogMessagesFilms.VALIDATION_FAILED.getMessage());
        }

        if (!film.getReleaseDate().isAfter(localDate)) {
            throw new ValidationException(LogMessagesFilms.VALIDATION_FAILED.getMessage());
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException(LogMessagesFilms.VALIDATION_FAILED.getMessage());
        }

        return true;
    }


    private void checkDataRelease(@NonNull Film film) {
        if (!film.getReleaseDate().isAfter(localDate)) {
            log.error(LogMessagesFilms.FILM_NOT_VALIDATED_DATE.getMessage() + film.toString());
            throw new FilmAlreadyExistException(LogMessagesFilms.FILM_NOT_VALIDATED_DATE.getMessage());
        }
    }

    private void validationFailed(Film film) throws ValidationException {
        log.error(LogMessagesFilms.VALIDATION_FAILED.getMessage() + film.toString());
        throw new FilmAlreadyExistException(LogMessagesFilms.VALIDATION_FAILED.getMessage());

    }
}