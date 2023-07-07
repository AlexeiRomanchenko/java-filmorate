package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info(LogMessagesUsers.GET_ALL_USERS_REQUEST.getMessage());
        return userService.getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info(LogMessagesUsers.CREATE_USER_REQUEST.getMessage());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info(LogMessagesUsers.UPDATE_USER_REQUEST.getMessage());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        log.info(LogMessagesUsers.GET_USER_BY_ID_REQUEST.getMessage() + id);
        return userService.findUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addToFriendList(@PathVariable int id, @PathVariable int friendId) {
        log.info(LogMessagesUsers.USER_ADD_FRIEND_REQUEST.getMessage()
                + LogMessagesUsers.USER_ID.getMessage() + id
                + LogMessagesUsers.FRIEND_ID.getMessage() + friendId);
        userService.addToFriendList(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFromListFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(LogMessagesUsers.USER_DELETE_FRIEND_REQUEST.getMessage()
                + LogMessagesUsers.USER_ID.getMessage() + id
                + LogMessagesUsers.FRIEND_ID.getMessage() + friendId);
        userService.removeFromListFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable int id) {
        log.info(LogMessagesUsers.GET_LIST_FRIENDS_USER_REQUEST.getMessage() + LogMessagesUsers.USER_ID + id);
        return userService.getListFriendsUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info(LogMessagesUsers.GET_LIST_COMMON_FRIENDS_REQUEST.getMessage()
                + LogMessagesUsers.USER_ID.getMessage() + id
                + LogMessagesUsers.USER_ID.getMessage() + otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getPopularFilms(@PathVariable int id) {
        log.info(LogMessagesUsers.GET_LIST_RECOMMENDED_FILMS_REQUEST.getMessage()
                + LogMessagesUsers.USER_ID.getMessage() + id);
        return userService.getRecommendations(id);
    }
}