package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User getById(int id);

    Collection<User> getUsers();

    User create(User user);

    User update(User user);

    boolean delete(int id);

}
