package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.description.LogGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/{id}")
    public Genre getGenreById(@Positive @PathVariable Integer id) {
        log.info(LogGenre.GET_GENRE_REQUEST.getMessage(), id);
        return genreService.getGenreById(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info(LogGenre.GET_ALL_GENRE_REQUEST.getMessage());
        return genreService.getAllGenres();
    }

}