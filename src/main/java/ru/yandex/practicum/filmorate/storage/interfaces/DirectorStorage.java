package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirectorById(Integer id);

    Director updateDirector(Director director);

    Director deleteDirectorById(Integer id);

    void deleteAllDirectors();

    Collection<Film> loadAllDirectors(Collection<Film> films);
}