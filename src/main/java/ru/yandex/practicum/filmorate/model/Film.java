package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder

public class Film {
    @Positive
    private Integer id;
    @NotBlank
    private String name;
    @Size(min = 0,max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}
