package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        ValidatorUser.validator(user);
        userStorage.create(user);
        return user;
    }

    public User update(User user) {
        ValidatorUser.validator(user);
        userStorage.update(user);
        return user;
    }

    public User findUser(int id) {
        if (!userStorage.getIdsAllUsers().contains(id)) {
            throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage());
        }

        return userStorage.getById(id);
    }

    public void addToFriendList(int id, int friendId) { //добавление в друзья
        if (findUser(id).getFriends() != null && findUser(id).getFriends().contains((long) friendId)) {
            throw new ActionHasAlreadyDoneException(LogMessagesUsers.FRIEND_ALREDY_ADD.getMessage());
        }

        if (friendId <= 0) {
            throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage());
        }

        findUser(id).addFriendById(friendId);
        findUser(friendId).addFriendById(id);
    }

    public void removeFromListFriend(int id, int friendId) {
        findUser(id).deleteFriendById(friendId);
        findUser(friendId).deleteFriendById(id);
    }

    public List<User> getListFriendsUserById(int id) {
        return findUser(id)
                .getFriends()
                .stream()
                .map(userId -> userStorage.getById(Math.toIntExact(userId)))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return getListFriendsUserById(id)
                .stream()
                .filter(getListFriendsUserById(otherId)::contains)
                .collect(Collectors.toList());
    }

}