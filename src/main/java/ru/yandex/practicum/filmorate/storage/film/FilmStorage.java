package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;


public interface FilmStorage {
    HashMap<Integer, Film> getFilms();

    Film getById(int id);

    Collection<Integer> getIdsAllFilms();

    Film create(Film film);

    Film update(Film film);

    boolean delete(int id);

}