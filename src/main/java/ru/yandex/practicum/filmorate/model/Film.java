package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder

public class Film {
    @Positive
    private Integer id;
    @Positive
    private Integer duration;
    @Size(max = 200)
    private String description;
    private String name;
    private LocalDate releaseDate;

}