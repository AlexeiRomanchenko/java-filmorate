package ru.yandex.practicum.filmorate.description;

import lombok.Getter;

@Getter
public enum EventType {
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    FRIEND("FRIEND");
    private final String event;

    EventType(String event) {
        this.event = event;
    }
}
