package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @Positive
    private Integer id;
    private Set<Long> friends;
    @Email
    @NotBlank
    private String email;
    private String login;
    private String name;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    private Set<Integer> likes;

    public void addFriendById(long id) {
        checkOnNullFriends();
        friends.add(id);
    }

    public Set<Long> getFriends() {
        checkOnNullFriends();
        return friends;
    }

    public void deleteFriendById(long id) {
        friends.remove(id);
    }

    private void checkOnNullFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
    }

}