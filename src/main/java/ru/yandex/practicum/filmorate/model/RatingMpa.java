package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingMpa {

    @Positive
    private int id;

    @NotBlank
    private String name;

    @Override
    public String toString() {
        return "RatingMpa{" + "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}