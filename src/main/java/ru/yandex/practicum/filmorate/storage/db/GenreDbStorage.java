package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE genre_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, genreId);
        if (srs.next()) {
            return new Genre(genreId, srs.getString("genre_name"));
        }
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM genres ";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery);
        while (srs.next()) {
            genres.add(new Genre(srs.getInt("genre_id"), srs.getString("genre_name")));
        }
        return genres;
    }

    @Override
    public Collection<Film> addGenreForList(Collection<Film> films) {
        Map<Integer, Film> filmsTable = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(", ", Collections.nCopies(filmsTable.size(), "?"));
        final String sqlQuery = "SELECT * "
                + "FROM film_genres "
                + "LEFT OUTER JOIN genres ON film_genres.genre_id = genres.genre_id "
                + "WHERE film_genres.film_id IN (" + inSql + ") "
                + "ORDER BY film_genres.genre_id";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            filmsTable.get(rs.getInt("film_id")).addGenre(new Genre(rs.getInt("genre_id"),
                    rs.getString("genre_name")));
        }, filmsTable.keySet().toArray());
        return films;
    }

}