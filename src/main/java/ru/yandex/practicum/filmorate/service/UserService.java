package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final EventService eventService;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
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

        if (userStorage.getById(user.getId()) != null) {
            userStorage.update(user);
        } else
            ValidatorUser.validationFailed(user);

        return user;
    }

    public User findUser(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage());
        }

        return user;
    }

    public void addToFriendList(int id, int friendId) {
        if (findUser(id).getFriends().contains((long) friendId)) {
            throw new ActionHasAlreadyDoneException(LogMessagesUsers.FRIEND_ALREADY_ADD.getMessage());
        }

        if (friendId <= 0) {
            throw new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage());
        }

        checkUser(id, friendId);
        userStorage.addFriend(id, friendId);
        eventService.createEvent(id, EventType.FRIEND, Operation.ADD, friendId);
    }

    public void removeFromListFriend(int id, int friendId) {
        checkUser(id, friendId);
        userStorage.removeFriend(id, friendId);
        eventService.createEvent(id, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    public List<User> getListFriendsUserById(int id) {
        List<User> userFriends = userStorage.getFriends(id);
        return userFriends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> commonFriends = userStorage.getCommonFriends(id, otherId);
        return commonFriends;
    }

    private void checkUser(Integer userId, Integer friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
    }

    public Collection<Film> getRecommendations(int id) {
        return filmStorage.findRecommendations(id);
    }

}