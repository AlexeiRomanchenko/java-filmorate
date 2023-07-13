package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirecorStorage {

    public Director addDirector(Director director);

    public List<Director> getAllDirectors();

    public Director getDirectorById(Integer id);

    public Director updateDirector(Director director);

    public Director deleteDirectorById(Integer id);

    public void deleteAllDirectors();

}