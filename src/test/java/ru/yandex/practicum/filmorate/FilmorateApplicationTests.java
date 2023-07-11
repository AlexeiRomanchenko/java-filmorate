package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.MPAController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    @BeforeEach
    void clearDB() {
        filmStorage.clearDbFilms();
        filmStorage.clearDbLikes();
        userStorage.clearDbFriends();
        userStorage.clearDbUsers();
    }

    private final FilmStorage filmStorage;
    private final FilmController filmController;
    private final UserController userController;
    private final MPAController mpaController;
    private final UserDbStorage userStorage;

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

    @Test
    void shouldCreateNewFilm() {

        Film film = Film.builder()
                .id(2)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        Film createFilm = filmStorage.create(film);

        assertEquals(2, createFilm.getId());
        assertEquals("Агент007", createFilm.getName());
        assertEquals("Джеймс Бонд", createFilm.getDescription());
        assertEquals(9879, createFilm.getDuration());

        assertEquals(mpaController.getRatingMpaById(5).toString(), createFilm.getMpa().toString());

    }

    @Test
    void shouldGetFilms() {
        Film film = Film.builder()
                .id(4)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        filmStorage.create(film);

        Collection<Film> films = filmStorage.getFilms();

        assertEquals(filmStorage.getFilms().toString(), films.toString());

    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .id(2)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        filmController.create(film);

        film.setName("Агент009");
        film.setDescription("Бонд");

        Film tempFilm = filmController.update(film);

        assertEquals("Агент009", tempFilm.getName());
        assertEquals("Бонд", tempFilm.getDescription());

        assertEquals("[Film(id=1, name=Агент009, description=Бонд, releaseDate=1895-12-29, duration=9879," +
                        " genres=[], mpa=RatingMpa{id=5, name='NC-17'}, likes=[])]",
                filmStorage.getFilms().toString());
    }

    @Test
    void shouldThrowException_ForCreateFilmBeforeRelease() {
        Film film = Film.builder()
                .id(1)
                .name("Агент007")
                .description("Джеймс Бонд")
                .duration(9879)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
    }

    @Test
    void shouldThrowException_ForCreateFilmWithoutName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("{Хороший фильм}")
                .duration(9879)
                .releaseDate(LocalDate.of(1995, 12, 28))
                .mpa(mpaController.getRatingMpaById(5))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
    }

    @Test
    void shouldCreateNewUsers() {
        User user = User.builder()
                .id(6)
                .name("Петя")
                .email("petya@jd.ru")
                .login("Petya")
                .birthday(LocalDate.of(1958, 6, 19))
                .build();

        User createdUser = userStorage.create(user);

        System.out.println(user.toString());
        assertEquals(17, createdUser.getId());
        assertEquals("Петя", createdUser.getName());
        assertEquals("Petya", createdUser.getLogin());
        assertEquals(createdUser.getBirthday(), LocalDate.of(1958, 6, 19));
        assertEquals("petya@jd.ru", createdUser.getEmail());

    }

    @Test
    void shouldGetUsers() {
        User user = User.builder()
                .id(15)
                .name("Вова")
                .email("vova@jd.ru")
                .login("Vova")
                .birthday(LocalDate.of(1959, 6, 19))
                .build();

        userStorage.create(user);

        User user1 = User.builder()
                .id(16)
                .name("Вася")
                .email("vasya@jd.ru")
                .login("Vasya")
                .birthday(LocalDate.of(1959, 6, 19))
                .build();

        userStorage.create(user1);

        Collection<User> users = userStorage.getUsers();

        System.out.println(user.toString());
        assertEquals("[User(id=15, friends=[], email=vova@jd.ru, login=Vova, name=Вова, " +
                        "birthday=1959-06-19, likes=null), User(id=16, friends=[], email=vasya@jd.ru, login=Vasya, " +
                        "name=Вася, birthday=1959-06-19, likes=null)]",
                users.toString());

    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .name("Иван")
                .email("ivan@jd.ru")
                .login("Vanya")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();


        User createdUser = userStorage.create(user);

        user.setName("Николай");

        userStorage.update(user);

        assertEquals("Николай", createdUser.getName());

    }

    @Test
    void shouldThrowExceptionForUpdateUnknownUser() {
        User user = User.builder()
                .id(123)
                .name("Иван")
                .email("ivan@jd.ru")
                .login("Vanya")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();

        assertThrows(ObjectNotFoundException.class, () -> {
            userStorage.update(user);
        });

    }

    @Test
    void shouldThrowException_ForCreateUserWithWithEmptyLogin() {
        User user = User.builder()
                .id(123)
                .name("Иван")
                .email("ivan@jd.ru")
                .login("")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

    }

    @Test
    void shouldThrowException_ForUpdateWithEmptyLogin() {
        User user = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@jd.ru")
                .login("")
                .birthday(LocalDate.of(1955, 6, 19))
                .build();

        userStorage.create(user);

        assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });

    }

    @Test
    void shouldThrowException_ForCreateUserWhiteFutureBirth() {
        User user = User.builder()
                .name("Иван")
                .email("ivan@jd.ru")
                .login("")
                .birthday(LocalDate.of(2030, 6, 19))
                .build();

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

    }

    @Test
    void shouldThrowException_ForUpdateUserWhiteFutureBirth() {
        User user = User.builder()
                .name("Иван")
                .email("ivan@jd.ru")
                .login("Ivan")
                .birthday(LocalDate.of(1968, 6, 19))
                .build();

        userController.create(user);

        user.setBirthday(LocalDate.of(2066, 6, 19));
        assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });

    }

    @Test
    void shouldThrowException_ForUpdateUserUnknownId() {
        User user = User.builder()
                .id(999)
                .name("Саша")
                .email("sasha@rjd.ru")
                .login("Sanya")
                .birthday(LocalDate.of(1965, 6, 21))
                .build();

        assertThrows(ObjectNotFoundException.class, () -> {
            userController.update(user);
        });

    }

    @Test
    public void finUserTest() throws ValidationException {

        userController.create(user);
        assertEquals(userController.findUser(user.getId()).toString(), user.toString());
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

        User tempUser = userController.findUser(user.getId());

        assertEquals(1, userController.getFriendList(tempUser.getId()).size());
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

        User tempUser = userController.findUser(user.getId());

        assertEquals(2, userController.getFriendList(tempUser.getId()).size());

    }

}