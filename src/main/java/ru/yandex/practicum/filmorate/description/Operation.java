package ru.yandex.practicum.filmorate.description;

import lombok.Getter;

@Getter
public enum Operation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");
    private final String operation;

    Operation(String operation) {
        this.operation = operation;
    }
}
