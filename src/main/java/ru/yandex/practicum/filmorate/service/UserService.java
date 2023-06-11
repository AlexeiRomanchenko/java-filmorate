package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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

        findUser(id).addFriendById(friendId);
        findUser(friendId).addFriendById(id);
    }

    public void removeFromListFriend(int id, int friendId) {
        findUser(id).deleteFriendById(friendId);
        findUser(friendId).deleteFriendById(id);
    }

    public List<User> getListFriendsUserById(int id) {
        User user = findUser(id);
        Set<Long> friendIds = user.getFriends();

        List<User> userFriends = friendIds.stream()
                .map(userId -> userStorage.getById(Math.toIntExact(userId)))
                .collect(Collectors.toList());

        return userFriends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return getListFriendsUserById(id)
                .stream()
                .filter(getListFriendsUserById(otherId)::contains)
                .collect(Collectors.toList());
    }

}