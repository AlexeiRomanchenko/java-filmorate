package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends LikesStorage {
    List<Film> getDirectorsFilms(int directorId, String sortBy);

    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    void delete(int filmId);

    Film getById(Integer id);

    void addGenre(int filmId, Set<Genre> genres);

    void clearDbFilms();

    void clearDbLikes();

    List<Film> findRecommendations(Integer id);
}