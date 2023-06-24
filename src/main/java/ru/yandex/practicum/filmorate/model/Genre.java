package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Builder
@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Positive
    protected int id;

    @NotBlank
    protected String name;

}