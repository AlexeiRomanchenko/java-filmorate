package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {

    private final DirectorDbStorage directorStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void deleteAllDirectors() {
        directorStorage.deleteAllDirectors();
    }

    public Director deleteDirectorById(Integer id) {
        return directorStorage.deleteDirectorById(id);
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

}