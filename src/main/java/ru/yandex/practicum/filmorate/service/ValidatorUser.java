package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class ValidatorUser {
    protected static void validator(User user) {

        if (user.getLogin() != null && user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
        }

        checkNameUser(user);
    }

    private static void checkNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    protected static void validationFailed(User user) throws ValidationException {
        log.error(LogMessagesUsers.VALIDATION_FAILED.getMessage() + user.toString());
        throw new  ObjectNotFoundException(LogMessagesUsers.VALIDATION_FAILED.getMessage());
    }

}