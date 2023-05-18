package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        if (validator(film)) {
            if (films.get(film.getId()) != null) {
                log.info("Фильм " + film.toString() + "уже есть в базе");
                throw new FilmAlreadyExistException();
            }
            film.setId(nextId);
            films.put(film.getId(), film);
            nextId++;
            log.info("Фильм добавлен " + film.toString());
            return film;
        } else {
            log.error("не прошел валидацию фильм  " + film.toString());
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        if (validator(film)) {
            if (films.get(film.getId()) != null) {
                films.put(film.getId(), film);
                log.info("Фильм обновлен " + film.toString());
                return film;
            } else {
                log.error("Фильм не найден фильм " + film.toString());
                throw new FilmAlreadyExistException("Фильм не найден");
            }
        } else {
            log.error("Фильм не прошел валидацию фильм  " + film.toString());
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }

    private boolean validator(@NonNull Film film) {
        return !film.getName().isEmpty()
                && film.getDescription().length() < 201
                && film.getReleaseDate().isAfter(localDateTime)
                && film.getDuration() > 0;
    }
}