package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public List<Event> findEventsByUserId(Integer id) {
        return eventStorage.findEventsByUserID(id);
    }

    public Event createEvent(Integer userID, EventType eventType, Operation operation, Integer entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setUserId(userID);
        event.setEntityId(entityId);

        return eventStorage.createEvent(event);
    }
}
