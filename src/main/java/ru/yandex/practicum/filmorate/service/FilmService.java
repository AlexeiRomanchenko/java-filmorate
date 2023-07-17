package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Film createFilm(Film film) {
        ValidatorFilm.validator(film);
        return filmStorage.create(film);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        return new ArrayList<>(filmStorage.getCommonFilms(userId, friendId));
    }

    private void checkUserId(Integer id) {
        if (id < 1 || userStorage.getById(id) == null) {
            throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage() + id);
        }
    }

    public Film update(Film film) {
        ValidatorFilm.validator(film);

        if (filmStorage.getById(film.getId()) != null) {
            filmStorage.update(film);
        } else
            ValidatorFilm.validationFailed(film);

        return film;
    }

    public void deleteFilm(int id) {
        ValidatorFilm.validator(filmStorage.getById(id));
        filmStorage.delete(id);
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
                eventService.createEvent(userId, EventType.LIKE, Operation.ADD, filmId);
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

        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public Collection<Film> getSortedPopularFilms(Integer count, Integer genreId, Integer releaseYear) {
        return new ArrayList<>(filmStorage.getSortedPopularFilms(count, genreId, releaseYear));
    }

    public Collection<Film> getRecommendations(int id) {
        return filmStorage.findRecommendations(id);
    }

    public List<Film> getListFilmsByIdDirectorWithSorted(int directorId, String sortBy) {
        return filmStorage.getDirectorsFilms(directorId, sortBy);
    }

    public Collection<Film> getSearchedFilms(String query, String by) {
        List<SearchParam> searchParams = Arrays.stream(by.split(",")).map(String::trim).map(String::toUpperCase)
                .map(SearchParam::valueOf).collect(Collectors.toList());
        return filmStorage.findSearchedFilm(query, searchParams);
    }
}