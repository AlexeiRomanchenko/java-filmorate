package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RestController
public class FilmsTest {
    FilmController filmController = new FilmController();

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
        assertEquals("[Film(id=1, name=Агент007, description=Джеймс Бонд, releaseDate=1895-12-29, duration=9879)]",
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
        Throwable exception = assertThrows(FilmAlreadyExistException.class, () -> filmController.create(film));
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
        assertEquals("[Film(id=1, name=Агент007, description=Джеймс Бонд, releaseDate=1895-12-29, duration=9879)]",
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
    void shouldNoCreateFilmWithNegativeDuration() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("{Хороший фильм}")
                .duration(-12)
                .releaseDate(LocalDate.of(1995, 12, 28))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldNoCreateUserWithDescriptionMore200Symbols() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Бывший морпех Джейк Салли прикован к инвалидному креслу. Несмотря на немощное тело," +
                        " Джейк в душе по-прежнему остается воином. Он получает задание совершить путешествие в " +
                        "несколько световых лет к базе землян на планете Пандора.")
                .duration(9534)
                .releaseDate(LocalDate.of(1995, 12, 28))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }
}