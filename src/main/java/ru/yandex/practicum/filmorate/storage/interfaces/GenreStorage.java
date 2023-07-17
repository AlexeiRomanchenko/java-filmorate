package ru.yandex.practicum.filmorate.storage.interfaces;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {

    void deleteAllGenresById(int filmId);

    Genre getGenreById(int genreId);

    List<Genre> getAllGenres();

    Collection<Film> addGenreForList(Collection<Film> films);
}