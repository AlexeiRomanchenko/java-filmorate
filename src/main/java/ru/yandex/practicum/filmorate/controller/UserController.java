package ru.yandex.practicum.filmorate.controller;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {
    private int nextId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.info(LogMessagesUsers.GET_ALL_USERS_REQUEST.getMessage());
        return users.values();
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if (validator(user)) {
            if (users.get(user.getId()) != null) {
                log.info(LogMessagesUsers.USER_ALREADY_EXISTS.getMessage() + user.toString());
                throw new UserAlreadyExistException();
            }
            user.setId(nextId);
            users.put(user.getId(), user);
            nextId++;
            log.info(LogMessagesUsers.USER_ADD + user.toString());
            return user;
        } else {
            validationFailed(user);
        }
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        if (validator(user)) {
            if (users.get(user.getId()) != null) {
                users.put(user.getId(), user);
                log.info(LogMessagesUsers.USER_DATA_UPDATED + user.toString());
                return user;
            } else {
                validationFailed(user);
            }
        } else {
            validationFailed(user);
        }
        return user;
    }

    private boolean validator(@NonNull User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            return false;
        }

        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            return false;
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }

        return true;
    }

    private void validationFailed(User user) throws ValidationException {
        log.error(LogMessagesUsers.USER_NO_FOUND + user.toString());
        throw new UserAlreadyExistException(LogMessagesUsers.USER_NO_FOUND.getMessage());
    }
}