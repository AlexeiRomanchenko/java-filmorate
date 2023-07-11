package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        ValidatorFilm.validator(film);
        filmStorage.create(film);
        return film;
    }

    public Film update(Film film) {
        ValidatorFilm.validator(film);

        if (filmStorage.getById(film.getId()) != null) {
            filmStorage.update(film);
        } else
            ValidatorFilm.validationFailed(film);

        return film;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film findFilm(int id) {
        Film film = filmStorage.getById(id);

        if (film == null) {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage());
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            if (userStorage.getById(userId) != null) {
                filmStorage.addLike(filmId, userId);
            } else {
                throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage() + userId);
            }
        } else {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage() + filmId);
        }
    }

    public void deleteLike(int filmId, int userId) {

        if (userId < 0) {
            throw new ObjectNotFoundException(LogMessagesUsers.ID_NOT_POSITIVE.getMessage());
        }

        filmStorage.removeLike(filmId, userId);

    }

    public Collection<Film> getSortedPopularFilms(Integer count, Integer genreId, Integer releaseYear) {
        List<Film> result = new ArrayList<>(filmStorage.getSortedPopularFilms(count, genreId, releaseYear));
        return result;

    }

}