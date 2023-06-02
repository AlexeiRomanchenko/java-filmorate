package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    ArrayList<User> commonFriends = new ArrayList<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        List<User> friends = new ArrayList<>();

        for (Long friend : findUser(id).getFriends()) {
            friends.add(userStorage.getById(Math.toIntExact(friend)));
        }

        return friends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        commonFriends.clear();

        if (findUser(id).getFriends() == null || findUser(otherId).getFriends() == null) {
            return commonFriends;
        }

        commonFriends.addAll(getListFriendsUserById(id));
        commonFriends.retainAll(getListFriendsUserById(otherId));
        return commonFriends;
    }

}