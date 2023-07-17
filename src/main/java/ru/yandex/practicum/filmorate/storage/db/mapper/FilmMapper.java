package ru.yandex.practicum.filmorate.storage.db.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class FilmMapper {
    private final FilmDbStorage filmStorage;

    public static Film makeFilm(ResultSet rs, int id) throws SQLException {
        int filmId = rs.getInt("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Director> directors = new HashSet<>();
        Set<Genre> genres = new HashSet<>();
        return Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .directors(directors)
                .build();

    }

    public static Film filmMap(SqlRowSet srs) {
        int id = srs.getInt("film_id");
        String name = srs.getString("film_name");
        String description = srs.getString("description");
        int duration = srs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(srs.getDate("release_date")).toLocalDate();
        int mpaId = srs.getInt("rating_id");
        String mpaName = srs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = new HashSet<>();
        Set<Director> directors = new HashSet<>();
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .genres(genres)
                .releaseDate(releaseDate)
                .directors(directors)
                .build();
    }

}
