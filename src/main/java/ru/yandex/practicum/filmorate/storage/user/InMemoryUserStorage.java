package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

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

    public HashMap<Integer, User> getUsers() {
        log.info(LogMessagesUsers.GET_ALL_USERS_REQUEST.getMessage());
        return users;
    }

    public User create(User user) {
        log.info(LogMessagesUsers.CREATE_USER.getMessage());

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

    public User update(User user) {
        users.put(user.getId(), user);
        log.info(LogMessagesUsers.USER_DATA_UPDATED.getMessage() + user.toString());
        return user;
    }

    public boolean delete(int id) {
        log.info(LogMessagesUsers.DELETE_USER.getMessage());
        users.remove(id);
        return true;
    }

}