package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Director {
    @Positive
    private int id;
    @NotBlank(message = "Имя режиссера не должно быть пустым.")
    private String name;

}