package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    UserController userController = new UserController();

    @Test
    void shouldCreateNewUser() {
        User user = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@rjd.ru")
                .login("Vanya")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();
        userController.create(user);
        assertEquals("[User(id=1, email=ivan@rjd.ru, login=Vanya, name=Иван, birthday=1955-06-19)]",
                String.valueOf(userController.getUsers()));
    }

    @Test
    void shouldNoCreateUserWithoutDog() {
        User user = User.builder()
                .id(2)
                .name("Андрей")
                .email("privetya.ru")
                .login("Andry")
                .birthday(LocalDate.of(1955, 12, 30))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldNoCreateUserIsBirthdayFuture() {////////
        User user = User.builder()
                .id(2)
                .name("Андрей")
                .email("privet@ya.ru")
                .login("Andry")
                .birthday(LocalDate.of(2024, 10, 20))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldNoCreateUserWithBlanksInLogin() {
        User user = User.builder()
                .id(2)
                .name("Андрей")
                .email("privet@ya.ru")
                .login("And ry")
                .birthday(LocalDate.of(1958, 12, 30))
                .build();
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(LogMessagesUsers.VALIDATION_FAILED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldCreateUserPastLoginInName() {
        User user = User.builder()
                .id(19)
                .name("")
                .email("privet@ya.ru")
                .login("Andry")
                .birthday(LocalDate.of(1958, 12, 30))
                .build();
        userController.create(user);

        assertEquals("[User(id=1, email=privet@ya.ru, login=Andry, name=Andry, birthday=1958-12-30)]",
                String.valueOf(userController.getUsers()));
    }
}
