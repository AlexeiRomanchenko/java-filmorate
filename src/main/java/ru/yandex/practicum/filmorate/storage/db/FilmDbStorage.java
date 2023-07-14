package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;

    @Value("${film.requestCommonFilmForTwoUsers}")
    private String requestCommonFilmForTwoUsers;

    @Value("${film.get-recommended-films-by-user}")
    private String requestGetRecommendedFilms;

    @Value("${director.get-filmsId-sorted-by-likes}")
    private String requestFilmIdByLikes;

    @Value("${director.get-filmsId-sorted-by-year}")
    private String requestFilmIdByYear;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> getFilms() {
        SqlRowSet filmsIdRow = jdbcTemplate.queryForRowSet("SELECT film_id FROM films");
        List<Film> films = new ArrayList<>();
        while (filmsIdRow.next()) {
            films.add(getById(filmsIdRow.getInt("film_id")));
        }
        films.sort(Comparator.comparingInt(Film::getId));

        return films;
    }

    @Override
    public Film create(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("film_name", "description", "duration", "release_date", "rating_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of(
                        "film_name", film.getName(),
                        "description", film.getDescription(),
                        "duration", film.getDuration(),
                        "release_date", java.sql.Date.valueOf(film.getReleaseDate()),
                        "rating_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("film_id"));
        addGenre((Integer) keys.get("film_id"), film.getGenres());
        addDirectors((Integer) keys.get("film_id"), film.getDirectors());
        Film newFilm = getById((Integer) keys.get("film_id"));
        return newFilm;
    }

    private void addDirectors(int filmId, List<Director> directors) {

        deleteAllDirectorsByFilmId(filmId);
        if (directors == null || directors.isEmpty()) {
            return;
        }
        List<Director> directorList = new ArrayList<>(directors);
        jdbcTemplate.batchUpdate("INSERT INTO films_directors (film_id, director_id) VALUES(?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, directorList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return directorList.size();
                    }
                });
    }

    private void deleteAllDirectorsByFilmId(int filmId) {
        String sglQuery = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
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
        addDirectors(film.getId(), film.getDirectors());
        int filmId = film.getId();
        film.setGenres(getGenres(filmId));
        film.setDirectors(getDirectors(filmId));
        return getById(filmId);
    }

    private List<Director> getDirectors(int filmId) {

        String sqlQuery = "SELECT films_directors.director_id, directors.director_name FROM films_directors "
                + "JOIN directors ON directors.director_id = films_directors.director_id "
                + "WHERE film_id = ? ORDER BY director_id ASC";

        List<Director> directors = new ArrayList<>(jdbcTemplate.query(sqlQuery, this::makeDirector, filmId));

        return directors;

    }

    private Director makeDirector(ResultSet rs, int id) throws SQLException {
        Integer directorId = rs.getInt("director_id");
        String directorName = rs.getString("director_name");
        return new Director(directorId, directorName);
    }

    @Override
    public void delete(int filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = " + filmId;
        int numberModifiedRows = jdbcTemplate.update(sqlQuery);
        if (numberModifiedRows < 1) {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage());
        }
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

    @Override
    public List<Film> getSortedPopularFilms(Integer count, Integer genreId, Integer releaseYear) {
        String sqlRequirement;
        if (genreId != 0 && releaseYear != 0) {
            sqlRequirement = "LEFT JOIN film_genres ON films.film_id = film_genres.film_id "
                    + "WHERE film_genres.genre_id = " + genreId
                    + " AND EXTRACT(YEAR FROM films.release_date) = " + releaseYear + " ";

        } else if (genreId != 0) {
            sqlRequirement = "LEFT JOIN film_genres ON films.film_id = film_genres.film_id "
                    + "WHERE film_genres.genre_id = " + genreId + " ";

        } else if (releaseYear != 0) {
            sqlRequirement = "WHERE EXTRACT(YEAR FROM films.release_date) = " + releaseYear + " ";

        } else {
            sqlRequirement = "";
        }

        String sqlQuery = "SELECT * FROM films "
                + "LEFT JOIN likes ON likes.film_id = films.film_id "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + sqlRequirement
                + "GROUP BY films.film_id, likes.user_id "
                + "ORDER BY COUNT (likes.film_id) DESC "
                + "LIMIT "
                + count;
       /* List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        return addGenreForList(films);*/
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::makeFilm);
        addGenreForList(filmList);
        return filmList;
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
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        List<Director> directors = new ArrayList<>();
        Set<Genre> genres = getGenres(filmId);
        Set<Integer> likes = getLikes(filmId);
        Film film = Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .directors(directors)
                .likes(likes)
                .build();
        return film;
    }

    private Film filmMap(SqlRowSet srs) {
        int id = srs.getInt("film_id");
        String name = srs.getString("film_name");
        String description = srs.getString("description");
        int duration = srs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(srs.getDate("release_date")).toLocalDate();
        int mpaId = srs.getInt("rating_id");
        String mpaName = srs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = getGenres(id);
        Set<Integer> likes = getLikes(id);
        List<Director> directors = getDirectors(id);
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .genres(genres)
                .releaseDate(releaseDate)
                .likes(likes)
                .directors(directors)
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

    private Set<Integer> getLikes(int filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> foundFilmLikes = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
        Set<Integer> likes = new HashSet<>(foundFilmLikes);
        return likes;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String getCommonQuery = requestCommonFilmForTwoUsers;
        List<Film> list = jdbcTemplate.query(getCommonQuery, this::makeFilm, userId, friendId);

        System.out.println(list);
        return list;
    }

    @Override
    public List<Film> getDirectorsFilms(int directorId, String sortBy) {
        SqlRowSet filmIdRow;
        if ("year".equals(sortBy)) {
            filmIdRow = jdbcTemplate.queryForRowSet(requestFilmIdByYear, directorId);
        } else {
            filmIdRow = jdbcTemplate.queryForRowSet(requestFilmIdByLikes, directorId);
        }
        List<Film> films = new LinkedList<>();
        while (filmIdRow.next()) {
            films.add(getById(filmIdRow.getInt("film_id")));
        }
        if (films.size() == 0) {
            log.info(LogDirector.EMPTY_LIST_FILMS_DIRECTOR.getMessage() + directorId);
            throw new ObjectNotFoundException(LogDirector.EMPTY_LIST_FILMS_DIRECTOR.getMessage() + directorId);
        }
        log.info(LogDirector.TRANSFER_SORTED_LIST.getMessage() + sortBy
                + LogDirector.LIST_FILMS_DIRECTOR.getMessage() + directorId + films);
        return films;
    }

    @Override
    public List<Film> findRecommendations(Integer userId) {
        return addGenreForList(jdbcTemplate.query(requestGetRecommendedFilms, this::makeFilm, userId, userId));
    }
}