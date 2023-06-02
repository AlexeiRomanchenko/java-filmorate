package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService, inMemoryUserStorage);

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
        assertEquals("[User(id=1, friends=null, email=ivan@rjd.ru, login=Vanya, name=Иван, birthday=1955-06-19)]",
                String.valueOf(userController.getUsers()));
    }

    @Test
    void shouldNoCreateUserIsBirthdayFuture() {
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

        assertEquals("[User(id=1, friends=null, email=privet@ya.ru, login=Andry, name=Andry, birthday=1958-12-30)]",
                String.valueOf(userController.getUsers()));
    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@rjd.ru")
                .login("Vanya")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();
        userController.create(user);

        assertEquals("[User(id=1, friends=null, email=ivan@rjd.ru, login=Vanya, name=Иван, birthday=1955-06-19)]",
                String.valueOf(userController.getUsers()));

        user.setName("Николай");
        user.setLogin("Kolya");
        user.setEmail("kolya@rjd.ru");

        userController.update(user);
        assertEquals("[User(id=1, friends=null, email=kolya@rjd.ru, login=Kolya, name=Николай, birthday=1955-06-19)]",
                String.valueOf(userController.getUsers()));
    }

    @Test
    void shouldGetUsers() {
        User user = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@rjd.ru")
                .login("Vanya")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();
        userController.create(user);

        User user1 = User.builder()
                .id(2)
                .name("Саша")
                .email("sasha@rjd.ru")
                .login("Sanya")
                .birthday(LocalDate.of(1965, 6, 21))
                .build();
        userController.create(user1);

        assertEquals("[User(id=1, friends=null, email=ivan@rjd.ru, login=Vanya, name=Иван, birthday=1955-06-19), " +
                        "User(id=2, friends=null, email=sasha@rjd.ru, login=Sanya, name=Саша, birthday=1965-06-21)]",
                String.valueOf(userController.getUsers()));

    }
}