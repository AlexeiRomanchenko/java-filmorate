package ru.yandex.practicum.filmorate.storage.db;


import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "LEFT JOIN film_genres ON film_genres.film_id = films.film_id "
                + "LEFT JOIN genres ON genres.genre_id = film_genres.genre_id";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        return addGenreForList(films);
    }

    @Override
    public Film create(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("film_name", "description", "duration", "release_date", "rating_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of("film_name", film.getName(),
                        "description", film.getDescription(),
                        "duration", film.getDuration(),
                        "release_date", java.sql.Date.valueOf(film.getReleaseDate()),
                        "rating_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("film_id"));
        addGenre((Integer) keys.get("film_id"), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        getById(film.getId());
        String sqlQuery = "UPDATE films "
                + "SET film_name = ?, "
                + "description = ?, "
                + "duration = ?, "
                + "release_date = ?, "
                + "rating_id = ? "
                + "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId(), film.getId());
        addGenre(film.getId(), film.getGenres());
        int filmId = film.getId();
        film.setGenres(getGenres(filmId));
        return getById(filmId);
    }

    public String delete(int filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        return sqlQuery;
    }

    public Film getById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "WHERE film_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (srs.next()) {
            return filmMap(srs);
        } else {
            throw new ObjectNotFoundException("Фильм с ID = " + filmId + " не найден");
        }
    }

    public void addGenre(int filmId, Set<Genre> genres) {
        deleteAllGenresById(filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) "
                + "VALUES (?, ?)";
        List<Genre> genresTable = new ArrayList<>(genres);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresTable.get(i).getId());
            }

            public int getBatchSize() {
                return genresTable.size();
            }
        });
    }

    private Set<Genre> getGenres(int filmId) {
        Comparator<Genre> compId = Comparator.comparing(Genre::getId);
        Set<Genre> genres = new TreeSet<>(compId);
        String sqlQuery = "SELECT film_genres.genre_id, genres.genre_name FROM film_genres "
                + "JOIN genres ON genres.genre_id = film_genres.genre_id "
                + "WHERE film_id = ? ORDER BY genre_id ASC";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
        return genres;
    }

    private void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) "
                + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE likes "
                + "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        String sqlQuery = "SELECT * FROM films "
                + "LEFT JOIN likes ON likes.film_id = films.film_id "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "GROUP BY films.film_id, likes.user_id "
                + "ORDER BY COUNT (likes.film_id) DESC "
                + "LIMIT "
                + count;
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    private List<Film> addGenreForList(List<Film> films) {
        Map<Integer, Film> filmsTable = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
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

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre_name");
        return new Genre(genreId, genreName);
    }

    private Film makeFilm(ResultSet rs, int id) throws SQLException {
        int filmId = rs.getInt("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = new HashSet<>();
        Film film = Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .build();
        return film;
    }

    private Film filmMap(SqlRowSet srs) {
        int id = srs.getInt("film_id");
        String name = srs.getString("film_name");
        String description = srs.getString("description");
        int duration = srs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(srs.getTimestamp("release_date"))
                .toLocalDateTime().toLocalDate();
        int mpaId = srs.getInt("rating_id");
        String mpaName = srs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = getGenres(id);
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .build();
        return film;
    }

    public void clearDbFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    public void clearDbLikes() {
        String sql = "DELETE FROM likes";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Film> findRecommendations(Integer userId) {
        jdbcTemplate.update("DROP TABLE IF EXISTS likes_rank;");
        String createLikesRank = "CREATE TEMPORARY TABLE IF NOT EXISTS likes_rank AS " +
                "SELECT similar.user_id, COUNT(*) rank " +
                "FROM likes target " +
                "JOIN likes similar ON target.film_id = similar.film_id AND target.user_id != similar.user_id " +
                "WHERE target.user_id = ? " +
                "GROUP BY similar.user_id;";
        jdbcTemplate.update(createLikesRank, userId);
        String sqlQuery = "SELECT f.*, mpa.rating_name " +
                "FROM films f " +
                "LEFT JOIN likes similar ON similar.film_id = f.film_id " +
                "LEFT JOIN rating_mpa mpa ON f.rating_id = mpa.rating_id " +
                "JOIN likes_rank ON likes_rank.user_id = similar.user_id " +
                "LEFT JOIN likes target ON target.user_id = ? AND target.film_id = similar.film_id " +
                "WHERE target.film_id IS NULL " +
                "GROUP BY similar.film_id " +
                "ORDER BY SUM(likes_rank.rank) DESC;";
        return addGenreForList(jdbcTemplate.query(sqlQuery, this::makeFilm, userId));
    }
}