package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.Operation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.mapper.EventMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createEvent(Integer userID, EventType eventType, Operation operation, Integer entityId) {
        Event event = new Event();

        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("events")
                .usingColumns("event_timestamp", "user_id", "event_type", "operation", "entity_id")
                .usingGeneratedKeyColumns("event_id")
                .executeAndReturnKeyHolder(Map.of(
                        "event_timestamp", Instant.now().toEpochMilli(),
                        "user_id", userID,
                        "event_type", eventType.getEvent(),
                        "operation", operation.getOperation(),
                        "entity_id", entityId))
                .getKeys();
        assert keys != null;
        event.setEventId((Integer) keys.get("event_id"));
    }

    @Override
    public List<Event> findEventsByUserID(Integer id) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, EventMapper::mapToEvent, id);
    }
}
