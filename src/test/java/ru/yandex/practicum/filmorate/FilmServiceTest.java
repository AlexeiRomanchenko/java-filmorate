package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.MPAController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;


import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceTest {

    private final MPAController mpaController;
    private final FilmStorage filmStorage;
    private final UserController userController;
    private final FilmController filmController;
    private static GenreController genreController;
    private static GenreStorage genreStorage;


    User user = User.builder()
            .id(1)
            .name("Name")
            .email("ivan@rjd.ru")
            .login("Vanya")
            .birthday(LocalDate.of(1990, 6, 11))
            .build();
    User user1 = User.builder()
            .id(2)
            .name("Name2")
            .email("ivan2@rjd.ru")
            .login("Vanya2")
            .birthday(LocalDate.of(1995, 4, 14))
            .build();

    @Test
    public void should_getPopularFilms() throws ValidationException {
        Film film = Film.builder()
                .id(0)
                .name("Film")
                .description("Film")
                .releaseDate(LocalDate.of(2010, 10, 11))
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(60)
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        Film film1 = Film.builder()
                .id(1)
                .name("Film1")
                .description("Film1")
                .releaseDate(LocalDate.of(2012, 9, 2))
                .duration(90)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        userController.create(user);
        userController.create(user1);

        filmController.create(film);
        filmController.create(film1);

        filmController.setLike(film.getId(), user.getId());
        filmController.setLike(film1.getId(), user.getId());

        assertEquals(3, filmController.getPopularFilms(10).size());

    }

    @Test
    public void shouldGetMostPopularFilms() throws ValidationException {

        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("Film")
                .releaseDate(LocalDate.of(2010, 10, 11))
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(60)
                .genres(new HashSet<>())
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        Film film1 = Film.builder()
                .id(2)
                .name("Film1")
                .description("Film1")
                .releaseDate(LocalDate.of(2012, 9, 2))
                .duration(90)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .genres(new HashSet<>())
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        filmController.create(film);
        filmController.create(film1);

        userController.create(user);
        userController.create(user1);

        filmController.setLike(film.getId(), user.getId());
        filmController.setLike(film1.getId(), user.getId());
        filmController.setLike(film1.getId(), user1.getId());

        assertEquals(1, filmController.getPopularFilms(1).size());

    }

    @Test
    void createFilm_shouldConfirmThatFilmIdExists() {
        Film film = Film.builder()
                .id(1)
                .name("Форсаж")
                .description("Скорость")
                .releaseDate(LocalDate.of(2030, 12, 29))
                .duration(180)
                .mpa(mpaController.getRatingMpaById(1))
                .build();
        filmStorage.create(film);
        Film filmOptional = filmStorage.getById(1);

        assertEquals(filmOptional.getId(), 1);
    }

    @Test
    void getFilmById_shouldConfirmThatFilmIdExists() {
        Film film = Film.builder()
                .id(2)
                .name("Форсаж")
                .description("Скорость")
                .releaseDate(LocalDate.of(2030, 12, 29))
                .duration(180)
                .mpa(mpaController.getRatingMpaById(1))
                .build();

        filmStorage.create(film);

        assertEquals(filmStorage.getById(film.getId()).getId(), film.getId());
    }

}