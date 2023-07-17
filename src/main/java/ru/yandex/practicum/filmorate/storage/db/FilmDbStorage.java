package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.SearchParam;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.db.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    @Value("${film.requestAllFilms}")
    private String requestAllFilms;
    @Value("${film.requestCommonFilmForTwoUsers}")
    private String requestCommonFilmForTwoUsers;
    @Value("${film.get-recommended-films-by-user}")
    private String requestGetRecommendedFilms;
    @Value("${director.get-filmsId-sorted-by-likes}")
    private String requestFilmIdByLikes;
    @Value("${director.get-filmsId-sorted-by-year}")
    private String requestFilmIdByYear;
    @Value("${film.search-for-films}")
    private String requestSearchForFilm;

    public Collection<Film> getFilms() {
        return jdbcTemplate.query(requestAllFilms, FilmMapper::makeFilm);
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
        assert keys != null;
        Integer id = (Integer) keys.get("film_id");
        film.setId(id);
        addGenre(id, film.getGenres());
        addDirectors(id, film.getDirectors());
        return getById(id).orElseThrow(() ->
                new ObjectNotFoundException(
                        LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage() + id));
    }

    private void addDirectors(int filmId, Set<Director> directors) {

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
        return getById(filmId).orElseThrow(() ->
                new ObjectNotFoundException(
                        LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage() + filmId));
    }

    private Set<Director> getDirectors(int filmId) {

        String sqlQuery = "SELECT films_directors.director_id, directors.director_name FROM films_directors "
                + "JOIN directors ON directors.director_id = films_directors.director_id "
                + "WHERE film_id = ? ORDER BY director_id";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, DirectorMapper::makeDirector, filmId));
    }

    @Override
    public void delete(int filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = " + filmId;
        int numberModifiedRows = jdbcTemplate.update(sqlQuery);
        if (numberModifiedRows < 1) {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage());
        }
    }

    public Optional<Film> getById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "WHERE film_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (srs.next()) {
            Film film = FilmMapper.filmMap(srs);
            film.setGenres(getGenres(filmId));
            film.setDirectors(getDirectors(filmId));
            return Optional.of(film);
        } else {
            return Optional.empty();

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
                + "WHERE film_id = ? ORDER BY genre_id";
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

    public Integer removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes "
                + "WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId);
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

        String sqlQuery = "SELECT films.*, rating_mpa.rating_name FROM films "
                + "LEFT JOIN likes ON likes.film_id = films.film_id "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + sqlRequirement
                + "GROUP BY films.film_id, likes.user_id "
                + "ORDER BY COUNT (likes.film_id) DESC "
                + "LIMIT "
                + count;
        return jdbcTemplate.query(sqlQuery, FilmMapper::makeFilm);
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre_name");
        return new Genre(genreId, genreName);
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
        return new HashSet<>(foundFilmLikes);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String getCommonQuery = requestCommonFilmForTwoUsers;
        List<Film> list = jdbcTemplate.query(getCommonQuery, FilmMapper::makeFilm, userId, friendId);

        System.out.println(list);
        return list;
    }

    @Override
    public List<Film> getDirectorsFilms(int directorId, String sortBy) {
        List<Film> films;
        if ("year".equals(sortBy)) {
            films = jdbcTemplate.query(requestFilmIdByYear, FilmMapper::makeFilm, directorId);
        } else {
            films = jdbcTemplate.query(requestFilmIdByLikes, FilmMapper::makeFilm, directorId);
        }
        if (films.isEmpty()) {
            log.info(LogDirector.EMPTY_LIST_FILMS_DIRECTOR.getMessage() + directorId);
            throw new ObjectNotFoundException(LogDirector.EMPTY_LIST_FILMS_DIRECTOR.getMessage() + directorId);
        }
        log.info(LogDirector.TRANSFER_SORTED_LIST.getMessage() + sortBy
                + LogDirector.LIST_FILMS_DIRECTOR.getMessage() + directorId + films);
        return films;
    }

    @Override
    public List<Film> findRecommendations(Integer userId) {
        return jdbcTemplate.query(requestGetRecommendedFilms, FilmMapper::makeFilm, userId, userId);
    }

    @Override
    public Collection<Film> findSearchedFilm(String query, List<SearchParam> searchParams) {
        List<String> queryList = new ArrayList<>(
                Collections.nCopies(searchParams.size(),
                        "%" + query.toUpperCase() + "%"));
        String sql = String.format(requestSearchForFilm,
                searchParams.stream()
                        .map(SearchParam::getColumn)
                        .collect(Collectors.joining(" LIKE ? OR ")));
        return jdbcTemplate.query(sql,
                FilmMapper::makeFilm, queryList.toArray());
    }
}