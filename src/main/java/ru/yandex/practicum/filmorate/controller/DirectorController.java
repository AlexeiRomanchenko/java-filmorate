package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor

public class DirectorController {

    public final DirectorService directorService;

    @PostMapping("/directors")
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info(LogDirector.POST_ADD_DIRECTOR.getMessage(), director);
        return directorService.addDirector(director);
    }

    @GetMapping("/directors")
    public Collection<Director> getAllDirectors() {
        log.info(LogDirector.GET_LIST_ALL_DIRECTOR_REQUEST.getMessage());
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@Positive @PathVariable int id) {
        log.info(LogDirector.GET_DIRECTOR_BY_ID_REQUEST.getMessage(), id);
        return directorService.getDirectorById(id);
    }

    @PutMapping("/directors")
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info(LogDirector.PUT_DIRECTOR_BY_ID_REQUEST.getMessage(), director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors")
    public void deleteAllDirectors() {
        log.info(LogDirector.DELETE_ALL_DIRECTORS_REQUEST.getMessage());
        directorService.deleteAllDirectors();
    }

    @DeleteMapping("/directors/{id}")
    public Director deleteDirectorById(@Positive @PathVariable int id) {
        log.info(LogDirector.DELETE_DIRECTOR_BY_ID_REQUEST.getMessage(), id);
        return directorService.deleteDirectorById(id);
    }

}