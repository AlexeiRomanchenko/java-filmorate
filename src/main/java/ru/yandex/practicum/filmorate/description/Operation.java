package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");
    private final String operation;
}
