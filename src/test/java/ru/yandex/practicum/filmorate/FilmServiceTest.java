package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    Film film = Film.builder()
            .id(1)
            .name("Film")
            .description("Film")
            .releaseDate(LocalDate.of(2010, 10, 11))
            .duration(60)
            .build();

    Film film1 = Film.builder()
            .id(2)
            .name("Film1")
            .description("Film1")
            .releaseDate(LocalDate.of(2012, 9, 2))
            .duration(90)
            .build();
    Film film2 = Film.builder()
            .id(3)
            .name("Film2")
            .description("Film2")
            .releaseDate(LocalDate.of(2018, 6, 2))
            .duration(160)
            .build();

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

    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(inMemoryFilmStorage);
    FilmController filmController = new FilmController(filmService, inMemoryFilmStorage);

    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService, inMemoryUserStorage);


    @Test
    public void getPopularFilms() throws ValidationException {
        filmController.create(film);
        filmController.create(film1);
        filmController.create(film2);

        userController.create(user);
        userController.create(user1);

        filmController.setLike(film.getId(), user.getId());
        filmController.setLike(film1.getId(), user.getId());
        filmController.setLike(film1.getId(), user1.getId());

        assertEquals(3, filmController.getPopularFilms("10").size());

    }

    @Test
    public void getMostPopularFilms() throws ValidationException {
        filmController.create(film);
        filmController.create(film1);
        filmController.create(film2);

        userController.create(user);
        userController.create(user1);

        filmController.setLike(film.getId(), user.getId());
        filmController.setLike(film1.getId(), user.getId());
        filmController.setLike(film1.getId(), user1.getId());

        assertEquals(1, filmController.getPopularFilms("1").size());

    }

}