package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        return userService.findUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addToFriendList(@PathVariable int id, @PathVariable int friendId) {
        userService.addToFriendList(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFromListFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFromListFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable int id) {
        return userService.getListFriendsUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}