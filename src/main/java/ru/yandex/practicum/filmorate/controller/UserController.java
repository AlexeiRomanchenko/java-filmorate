package ru.yandex.practicum.filmorate.controller;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Validated
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
    public User create(@Valid @NotNull @RequestBody User user) {
        if (validator(user)) {
            if (users.get(user.getId()) != null) {
                log.info(LogMessagesUsers.USER_ALREADY_EXISTS.getMessage() + user.toString());
                throw new UserAlreadyExistException();
            }
            user.setId(nextId);
            users.put(user.getId(), user);
            nextId++;
            log.info(LogMessagesUsers.USER_ADD.getMessage() + user.toString());
        }
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        checkNameUser(user);
        if (users.get(user.getId()) != null) {
            users.put(user.getId(), user);
            log.info(LogMessagesUsers.USER_DATA_UPDATED.getMessage() + user.toString());
        } else {
            validationFailed(user);
        }
        return user;
    }

    private void checkNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean validator(@NonNull User user) {
        checkNameUser(user);

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
        }

        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
        }

        return true;
    }

    private void validationFailed(User user) throws ValidationException {
        log.error(LogMessagesUsers.VALIDATION_FAILED.getMessage() + user.toString());
        throw new UserAlreadyExistException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
    }
}