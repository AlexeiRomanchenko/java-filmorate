package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {

    private final DirecorStorage direcorStorage;

    @Autowired
    public DirectorService(DirecorStorage direcorStorage) {
        this.direcorStorage = direcorStorage;
    }

    public Collection<Director> getAllDirectors() {
        return direcorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return direcorStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        return direcorStorage.updateDirector(director);
    }
    public void deleteAllDirectors() {
        direcorStorage.deleteAllDirectors();
    }

    public Director deleteDirectorById(Integer id) {
        return direcorStorage.deleteDirectorById(id);
    }

    public Director addDirector(Director director) {
        return direcorStorage.addDirector(director);
    }

}