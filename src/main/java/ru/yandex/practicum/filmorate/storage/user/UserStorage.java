package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

public interface UserStorage {
    User getById(int id);

    HashMap<Integer, User> getUsers();

    Collection<Integer> getIdsAllUsers();

    User create(User user);

    User update(User user);

    boolean delete(int id);

}
