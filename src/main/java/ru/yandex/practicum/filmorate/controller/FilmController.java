package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info(LogMessagesFilms.GET_ALL_FILMS_REQUEST.getMessage());
        return filmService.getFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info(LogMessagesFilms.CREATE_FILM_REQUEST.getMessage());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info(LogMessagesFilms.UPDATE_FILM_REQUEST.getMessage());
        return filmService.update(film);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.info(LogMessagesFilms.DELETE_FILM_REQUEST.getMessage() + filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info(LogMessagesFilms.GET_FILM_BY_ID_REQUEST.getMessage() + id);
        return filmService.findFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable int id, @PathVariable int userId) {
        log.info(LogMessagesFilms.USER_SET_LIKE_FILM_REQUEST.getMessage()
                + LogMessagesFilms.FILMS_ID.getMessage() + id
                + LogMessagesUsers.USER_ID.getMessage() + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info(LogMessagesFilms.USER_DELETE_LIKE_FILM_REQUEST.getMessage()
                + LogMessagesFilms.FILMS_ID.getMessage() + id
                + LogMessagesUsers.USER_ID.getMessage() + userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getSortedPopularFilms(@RequestParam(defaultValue = "10", value = "count") int count,
                                                  @RequestParam(defaultValue = "0", value = "year") int year,
                                                  @RequestParam(defaultValue = "0", value = "genreId") int genreId) {
        log.info(LogMessagesFilms.GET_LIST_POPULAR_SORTED_FILMS_REQUEST.getMessage());
        return filmService.getSortedPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info(LogMessagesFilms.GET_COMMON_FILMS_FOR_USERS_WITH_ID.getMessage() + userId + " и " + friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getListFilmsByIdDirectorWithSorted(@PathVariable int directorId,
                                                               @RequestParam(defaultValue = "likes") String sortBy) {
        log.info(LogDirector.GET_ALL_FILMS_BY_DIRECTOR_REQUEST.getMessage() + directorId
                + LogDirector.SORTED_BY + sortBy);
        return filmService.getListFilmsByIdDirectorWithSorted(directorId, sortBy);
    }

    @GetMapping("/search")  //GET /films/search?query=крад&by=director,title
    public Collection<Film> getSearchedFilms(@RequestParam @NotBlank String query, @RequestParam(defaultValue = "title") String by) {
        log.info(String.format(LogMessagesFilms.SEARCH_FOR_FILM.getMessage(), query, by));
        return filmService.getSearchedFilms(query, by);
    }

}