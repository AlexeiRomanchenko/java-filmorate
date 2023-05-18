package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private Integer duration;
}
