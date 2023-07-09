package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
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