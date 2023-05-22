package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class User {
    @Positive
    private Integer id;
    @Email @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}