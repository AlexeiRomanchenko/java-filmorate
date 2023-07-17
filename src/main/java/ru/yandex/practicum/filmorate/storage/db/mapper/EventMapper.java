package ru.yandex.practicum.filmorate.storage.db.mapper;

import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper {

    public static Event mapToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("EVENT_ID"));
        event.setTimestamp(resultSet.getLong("EVENT_TIMESTAMP"));
        EventType eventType = EventType.valueOf(resultSet.getString("EVENT_TYPE"));
        event.setEventType(eventType);
        Operation operation = Operation.valueOf(resultSet.getString("OPERATION"));
        event.setOperation(operation);
        event.setUserId(resultSet.getInt("USER_ID"));
        event.setEntityId(resultSet.getInt("ENTITY_ID"));

        return event;
    }
}
