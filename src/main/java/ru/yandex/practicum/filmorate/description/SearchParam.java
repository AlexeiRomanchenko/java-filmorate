package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchParam {

    TITLE("UPPER(f.film_name)"),
    DIRECTOR("UPPER(d.director_name)");

    private final String column;
}
