package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        ValidatorFilm.validator(film);
        filmStorage.create(film);
        return film;
    }

    public Film update(Film film) {
        ValidatorFilm.validator(film);

        if (filmStorage.getFilms().get(film.getId()) != null) {
            filmStorage.update(film);
        } else
            ValidatorFilm.validationFailed(film);

        return film;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms().values();
    }

    public Film findFilm(int id) {
        if (!filmStorage.getIdsAllFilms().contains(id)) {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage());
        }
        return filmStorage.getById(id);
    }

    public void addLike(int id, int userId) {

        if (findFilm(id).getLikes() != null && findFilm(id).getLikes().contains((long) userId)) {
            throw new ActionHasAlreadyDoneException(LogMessagesFilms.USER_ALREDY_ADD_LIKE.getMessage());
        } else {
            findFilm(id).setLikes(userId);
        }
    }

    public void deleteLike(int id, int userId) {

        if (userId < 0) {
            throw new ObjectNotFoundException(LogMessagesUsers.ID_NOT_POSITIVE.getMessage());
        }

        findFilm(id).deleteLike(userId);

    }

    public List<Film> getPopularFilms(int count) {
        return getFilms()
                .stream()
                .sorted(Comparator.comparingLong(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}