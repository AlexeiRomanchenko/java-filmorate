package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private static final LocalDateTime localDateTime = LocalDateTime.of(1895, 12, 28, 0, 0);
    private int nextId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        log.info(LogMessagesFilms.GET_ALL_FILMS_REQUEST.getMessage());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        if (validator(film)) {
            if (films.get(film.getId()) != null) {
                log.info(LogMessagesFilms.FILM_ALREADY_EXISTS.getMessage() + film.toString());
                throw new FilmAlreadyExistException();
            }
            film.setId(nextId);
            films.put(film.getId(), film);
            nextId++;
            log.info(LogMessagesFilms.FILM_ADD + film.toString());
            return film;
        } else {
            validationFailed(film);
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film)  {
        if (validator(film)) {
            if (films.get(film.getId()) != null) {
                films.put(film.getId(), film);
                log.info(LogMessagesFilms.FILM_DATA_UPDATED.getMessage() + film.toString());
                return film;
            } else {
                validationFailed(film);
            }
        } else {
            validationFailed(film);
        }
        return film;
    }

    private boolean validator(@NonNull Film film) {
        if (film.getName().isBlank()) {
            return false;
        }

        if (film.getDescription().length() >= 200) {
            return false;
        }

        if (!film.getReleaseDate().isAfter(localDateTime)) {
            return false;
        }

        if (film.getDuration() <= 0) {
            return false;
        }

        return true;
    }

    private void validationFailed(Film film) throws ValidationException{
        log.error(LogMessagesFilms.FILM_NO_FOUND.getMessage() + film.toString());
        throw new FilmAlreadyExistException(LogMessagesFilms.FILM_NO_FOUND.getMessage());
    }
}