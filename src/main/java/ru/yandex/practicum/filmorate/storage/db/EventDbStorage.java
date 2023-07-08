package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event createEvent(Event event) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("events")
                .usingColumns("event_timestamp", "user_id", "event_type", "operation", "entity_id")
                .usingGeneratedKeyColumns("event_id")
                .executeAndReturnKeyHolder(Map.of(
                        "event_timestamp", event.getTimestamp(),
                        "user_id", event.getUserId(),
                        "event_type", event.getEventType().getEvent(),
                        "operation", event.getOperation().getOperation(),
                        "entity_id", event.getEntityId()))
                .getKeys();
        event.setEventId((Integer) keys.get("event_id"));

        return event;
    }

    @Override
    public List<Event> findEventsByUserID(Integer id) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";

        return jdbcTemplate.query(sql, this::mapToEvent, id);
    }

    private Event mapToEvent(ResultSet resultSet, int rowNum) throws SQLException {
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
