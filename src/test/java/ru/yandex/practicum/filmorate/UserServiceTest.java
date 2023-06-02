package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    User user = User.builder()
            .id(1)
            .name("Name")
            .email("ivan@rjd.ru")
            .login("Vanya")
            .birthday(LocalDate.of(1990, 6, 11))
            .build();
    User user1 = User.builder()
            .id(2)
            .name("Name2")
            .email("ivan2@rjd.ru")
            .login("Vanya2")
            .birthday(LocalDate.of(1995, 4, 14))
            .build();
    User user2 = User.builder()
            .id(3)
            .name("Name3")
            .email("ivan3@rjd.ru")
            .login("Vanya3")
            .birthday(LocalDate.of(1993, 3, 3))
            .build();
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);

    UserController userController = new UserController(userService, inMemoryUserStorage);

    @Test
    public void finUserTest() throws ValidationException {
        userController.create(user);
        userController.findUser(1);
        assertEquals(userController.findUser(1), user);

    }

    @Test
    public void shouldThrowExceptionWhenIdDoesntExistTest() throws ValidationException {
        userController.create(user);
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> userController.findUser(999)
        );
        assertEquals(LogMessagesUsers.USER_NO_FOUND_WITH_ID.getMessage(), exception.getMessage());
    }

    @Test
    public void getCommonFriendsEmptyTest() throws ValidationException {
        userController.create(user);
        userController.create(user1);

        assertEquals(new ArrayList<>(), userController.getCommonFriends(user.getId(), user1.getId()));
    }

    @Test
    public void addFriendTest() throws ValidationException {
        userController.create(user);
        userController.create(user1);
        userController.addToFriendList(user.getId(), user1.getId());
        assertEquals(1, userController.findUser(user.getId()).getFriends().size());
    }

    @Test
    public void emptyCommonFriendsListTest() throws ValidationException {
        userController.create(user);
        userController.create(user1);
        userController.addToFriendList(user.getId(), user1.getId());

        assertTrue(userController.getCommonFriends(user.getId(), user1.getId()).isEmpty());
    }

    @Test
    public void getFriendsListTest() throws ValidationException {
        userController.create(user);
        userController.create(user1);
        userController.create(user2);
        userController.addToFriendList(user.getId(), user1.getId());
        userController.addToFriendList(user.getId(), user2.getId());

        System.out.println(userController.getFriendList(user.getId()));
        assertEquals(2, userController.getFriendList(user.getId()).size());
    }

    @Test
    public void addFriend3to1Test() throws ValidationException {
        userController.create(user);
        userController.create(user1);
        userController.create(user2);
    }

    @Test
    public void addFriend32to1() throws ValidationException {
        userController.create(user);
        userController.create(user1);
        userController.create(user2);

        userController.addToFriendList(user.getId(), user1.getId());
        userController.addToFriendList(user.getId(), user2.getId());

        assertEquals(2, userController.findUser(user.getId()).getFriends().size());
    }

}