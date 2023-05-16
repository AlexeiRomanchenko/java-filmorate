package ru.yandex.practicum.filmorate.controller;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        return users.values();
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if (validator(user)) {
            if (users.get(user.getId()) != null) {
                log.info("Пользователь " + user.toString() + " уже есть в базе");
                throw new UserAlreadyExistException();
            }
            user.setId(nextId);
            users.put(user.getId(), user);
            nextId++;
            log.info("Добавлен пользователь: " + user.toString());
            return user;
        } else {
            log.error("Пользователь " + user.toString() + " не прошел валидацию");
            throw new ValidationException("Валидация не пройдена");
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        if (validator(user)) {
            if (users.get(user.getId()) != null) {
                users.put(user.getId(), user);
                log.info("Обновлен пользователь " + user.toString());
                return user;
            } else {
                log.error("Пользователь " + user.toString() + " не найден");
                throw new UserAlreadyExistException("не найден пользователь");
            }
        } else {
            log.error("Пользователь " + user.toString() + " не прошел валидацию");
            throw new ValidationException("Валидация не пройдена");
        }
    }

    private boolean validator(@NonNull User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return !user.getLogin().isEmpty() && user.getEmail().contains("@") && !user.getEmail().isEmpty()
                && !user.getLogin().contains(" ") && user.getBirthday().isBefore(LocalDate.now());
    }
}