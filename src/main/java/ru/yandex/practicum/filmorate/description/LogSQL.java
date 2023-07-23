package ru.yandex.practicum.filmorate.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogSQL {
    SQL_EXCEPTION("SQL exception");

    private final String message;

}