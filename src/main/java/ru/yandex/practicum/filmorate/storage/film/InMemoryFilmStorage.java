package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    public Film getById(int id) {
        return films.get(id);
    }

    public Collection<Integer> getIdsAllFilms() {
        return new ArrayList<>(films.keySet());
    }

    public Collection<Film> getFilms() {
        log.info(LogMessagesFilms.GET_ALL_FILMS_REQUEST.getMessage());
        return new ArrayList<>(films.values());
    }

    public Film create(@Valid @RequestBody Film film) {
        ValidatorFilm.validator(film);

        if (films.get(film.getId()) != null) {
            log.info(LogMessagesFilms.FILM_ALREADY_EXISTS.getMessage() + film.toString());
            throw new FilmAlreadyExistException(LogMessagesFilms.FILM_ALREADY_EXISTS.getMessage());
        }
        film.setId(nextId);
        films.put(film.getId(), film);
        nextId++;

        log.info(LogMessagesFilms.FILM_ADD.getMessage() + film.toString());

        return film;
    }

    public Film update(@Valid @RequestBody Film film) {
        ValidatorFilm.validator(film);

        if (films.get(film.getId()) != null) {
            films.put(film.getId(), film);
            log.info(LogMessagesFilms.FILM_DATA_UPDATED.getMessage() + film.toString());
        } else {
            ValidatorFilm.validationFailed(film);
        }

        return film;
    }

    public boolean delete(int id) {
        if (films.isEmpty() || !films.containsKey(id)) {
            return false;
        } else {
            films.remove(id);
            return true;
        }
    }

}