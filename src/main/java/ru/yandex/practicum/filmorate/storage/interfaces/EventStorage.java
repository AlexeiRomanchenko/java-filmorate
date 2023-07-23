package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void createEvent(Integer userID, EventType eventType, Operation operation, Integer entityId);

    List<Event> findEventsByUserID(Integer id);
}
