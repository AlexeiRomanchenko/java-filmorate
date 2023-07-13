package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event createEvent(Event event);

    List<Event> findEventsByUserID(Integer id);
}
