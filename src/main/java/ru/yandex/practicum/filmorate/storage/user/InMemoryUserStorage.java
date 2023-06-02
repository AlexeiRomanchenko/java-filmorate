package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public User getById(int id) {
        return users.get(id);
    }

    public Collection<Integer> getIdsAllUsers() {
        return new ArrayList<>(users.keySet());
    }

    public Collection<User> getUsers() {
        log.info(LogMessagesUsers.GET_ALL_USERS_REQUEST.getMessage());
        return new ArrayList<>(users.values());
    }

    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя");
        ValidatorUser.validator(user);

        if (users.get(user.getId()) != null) {
            log.info(LogMessagesUsers.USER_ALREADY_EXISTS.getMessage() + user.toString());
            throw new UserAlreadyExistException(LogMessagesUsers.USER_ALREADY_EXISTS.getMessage());
        }
        user.setId(nextId);
        users.put(user.getId(), user);
        nextId++;
        log.info(LogMessagesUsers.USER_ADD.getMessage() + user.toString());

        return user;
    }

    public User update(@Valid @RequestBody User user) {
        ValidatorUser.validator(user);

        if (users.get(user.getId()) != null) {
            users.put(user.getId(), user);
            log.info(LogMessagesUsers.USER_DATA_UPDATED.getMessage() + user.toString());
        } else {
            ValidatorUser.validationFailed(user);
        }
        return user;
    }

    public boolean delete(int id) {
        if (users.isEmpty() || !users.containsKey(id)) {
            return false;
        } else {
            users.remove(id);
            return true;
        }
    }

}