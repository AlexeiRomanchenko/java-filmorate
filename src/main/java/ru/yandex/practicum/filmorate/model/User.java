package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    @Positive
    private Integer id;
    private Set<Long> friends;
    @Email @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;


    public void addFriendById(long id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(id);
    }

    public void deleteFriendById(long id){
        friends.remove(id);
    }
}