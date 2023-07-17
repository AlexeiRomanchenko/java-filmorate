package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public Film createFilm(Film film) {
        ValidatorFilm.validator(film);
        return filmStorage.create(film);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        return new ArrayList<>(directorStorage.loadAllDirectors(
                genreStorage.addGenreForList(
                        filmStorage.getCommonFilms(userId, friendId))));
    }

    private void checkUserId(Integer id) {
        if (userStorage.getById(id) == null) {
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
        Collection<Film> films = filmStorage.getFilms();
        log.debug("Received from filmStorage: {}", films);
        log.debug("Received from genreStorage: {}", genreStorage.addGenreForList(films));
        log.debug("Received from directorStorage: {}", directorStorage.loadAllDirectors(films));
        return films;
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
            checkUserId(userId);

            filmStorage.addLike(filmId, userId);
            eventService.createEvent(userId, EventType.LIKE, Operation.ADD, filmId);

        } else {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage() + filmId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (filmStorage.removeLike(filmId, userId) == 0) {
            throw new ObjectNotFoundException(userId + "not found");
        }
        log.debug("DEleted from staroge");
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public Collection<Film> getSortedPopularFilms(Integer count, Integer genreId, Integer releaseYear) {
        return directorStorage.loadAllDirectors(
                genreStorage.addGenreForList(
                        filmStorage.getSortedPopularFilms(count, genreId, releaseYear)));
    }

    public Collection<Film> getRecommendations(int id) {
        return directorStorage.loadAllDirectors(
                genreStorage.addGenreForList(
                        filmStorage.findRecommendations(id)));
    }

    public List<Film> getListFilmsByIdDirectorWithSorted(int directorId, String sortBy) {
        return new ArrayList<>(directorStorage.loadAllDirectors(genreStorage.addGenreForList(filmStorage.getDirectorsFilms(directorId, sortBy))));
    }

    public Collection<Film> getSearchedFilms(String query, String by) {
        List<SearchParam> searchParams = Arrays.stream(by.split(",")).map(String::trim).map(String::toUpperCase)
                .map(SearchParam::valueOf).collect(Collectors.toList());
        return directorStorage.loadAllDirectors(
                genreStorage.addGenreForList(
                        filmStorage.findSearchedFilm(query, searchParams)));
    }
}