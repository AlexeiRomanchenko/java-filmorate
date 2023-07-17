package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper {
    public static Director makeDirector(ResultSet rs, int id) throws SQLException {
        int directorId = rs.getInt("director_id");
        String directorName = rs.getString("director_name");
        return new Director(directorId, directorName);
    }

    public static Director buildDirectorFromRow(SqlRowSet row) {
        return Director.builder()
                .name(row.getString("director_name"))
                .id(row.getInt("director_id"))
                .build();
    }
}
