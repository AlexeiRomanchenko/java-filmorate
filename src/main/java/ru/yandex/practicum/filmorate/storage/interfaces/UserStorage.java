package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage extends FriendsStorage {

    boolean containsUser(int id);

    Collection<User> getUsers();

    User create(User user);

    User update(User user);

    void delete(int userId);

    Optional<User> getById(Integer id);

}