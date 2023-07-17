package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

public class UserMapper {
    public static User userMap(SqlRowSet srs) {
        int id = srs.getInt("user_id");
        String name = srs.getString("user_name");
        String login = srs.getString("login");
        String email = srs.getString("email");
        LocalDate birthday = Objects.requireNonNull(srs.getTimestamp("birthday"))
                .toLocalDateTime().toLocalDate();
        return User.builder()
                .id(id)
                .name(name)
                .login(login)
                .email(email)
                .birthday(birthday)
                .build();
    }
}
