package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RestController
public class FilmsTest {
    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(inMemoryFilmStorage);
    FilmController filmController = new FilmController(filmService);

    @Test
    void shouldCreateNewFilm() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .build();
        filmController.create(film);
        assertEquals("[Film(id=1, likes=[], name=Агент007, description=Джеймс Бонд, releaseDate=1895-12-29, duration=9879)]",
                String.valueOf(filmController.getFilms()));
    }

    @Test
    void shouldNoCreateFilmBeforeRelease() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(LogMessagesFilms.FILM_NOT_VALIDATED_DATE.getMessage(), exception.getMessage());
    }

    @Test
    void shouldCreateFilmAfterRelise() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .build();
        filmController.create(film);
        assertEquals("[Film(id=1, likes=[], name=Агент007, description=Джеймс Бонд, releaseDate=1895-12-29, duration=9879)]",
                String.valueOf(filmController.getFilms()));
    }

    @Test
    void shouldNoCreateFilmWithoutName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("{Хороший фильм}")
                .duration(9879)
                .releaseDate(LocalDate.of(1995, 12, 28))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .build();
        filmController.create(film);

        film.setName("Агент009");
        film.setDescription("Бонд");
        film.setDuration(777);
        filmController.update(film);
        assertEquals("[Film(id=1, likes=[], name=Агент009, description=Бонд, releaseDate=1895-12-29, duration=777)]",
                String.valueOf(filmController.getFilms()));
    }

    @Test
    void shouldGetFilms() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .build();
        filmController.create(film);

        Film film1 = Film.builder()
                .id(2)
                .name("Аватар")
                .description("Фантастика")
                .duration(7559)
                .releaseDate(LocalDate.of(2001, 1, 2))
                .build();
        filmController.create(film1);

        assertEquals("[Film(id=1, likes=[], name=Агент007, description=Джеймс Бонд, releaseDate=1895-12-29, duration=9879)," +
                        " Film(id=2, likes=[], name=Аватар, description=Фантастика, releaseDate=2001-01-02, duration=7559)]",
                String.valueOf(filmController.getFilms()));
    }

}