package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.description.LogSQL;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.db.mapper.DirectorMapper.buildDirectorFromRow;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    @Value("${director.get-id-by-director}")
    private String requestGetIdByDirector;
    @Value("${director.insert-director}")
    private String requestInsertDirector;
    @Value("${director.get-director-by-id}")
    private String requestGetDirectorById;
    @Value("${director.get-all-IDs}")
    private String requestAllIDs;
    @Value("${director.delete-director-by-id}")
    private String requestDeleteById;
    @Value("${director.reset-primary-key}")
    private String requestResetPK;
    @Value("${director.update-director}")
    private String requestUpdateDirector;
    @Value("${director.reset-all-data-table}")
    private String requestClearTableDirectors;
    @Value("${director.load-all-directors-for-films}")
    private String requestToLoadAllDirectors;

    @Override
    public Director addDirector(Director director) {

        if (isPresentInDB(director)) {
            throw new DirectorAlreadyExistException(LogDirector.FAILED_TO_ADD_DIRECTOR.getMessage()
                    + LogDirector.DIRECTOR_ALREADY_EXISTS.getMessage());
        }
        jdbcTemplate.update(requestInsertDirector, director.getName());
        SqlRowSet idRow = getIdRowsFromDB(director);
        if (idRow.next()) {
            director.setId(idRow.getInt("director_id"));
        }
        log.info(LogDirector.ADD_DIRECTOR.getMessage() + director);
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {

        String sqlQuery = "SELECT * FROM directors ";
        List<Director> directors = new ArrayList<>(jdbcTemplate.query(sqlQuery, DirectorMapper::makeDirector));
        log.info(LogDirector.TRANSFER_LIST_ALL_USERS.getMessage());

        return directors;
    }

    @Override
    public Director getDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new ObjectNotFoundException(LogDirector.FAILED_TO_TRANSFER_DIRECTOR.getMessage()
                    + LogDirector.NO_FOUND_DIRECTOR.getMessage());
        }
        Director director = buildDirectorFromRow(directorRow);
        log.info(LogDirector.TRANSFER_DIRECTOR.getMessage() + director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {

        if (!isPresentInDB(director.getId())) {
            throw new ObjectNotFoundException(LogDirector.FAILED_TO_UPDATE_DIRECTOR.getMessage()
                    + LogDirector.NO_FOUND_DIRECTOR);
        }
        if (isPresentInDB(director)) {
            throw new ObjectNotFoundException(LogDirector.FAILED_TO_UPDATE_DIRECTOR.getMessage()
                    + LogDirector.DIRECTOR_ALREADY_EXISTS.getMessage());
        }
        Director bufferDirector = getDirectorById(director.getId());
        try {
            jdbcTemplate.update(requestUpdateDirector, director.getName(), director.getId());
        } catch (DataIntegrityViolationException | BadSqlGrammarException ex) {
            updateDirector(bufferDirector);
            throw new RuntimeException(LogSQL.SQL_EXCEPTION.getMessage());
        }
        Director updatedDirector = getDirectorById(director.getId());
        log.info(LogDirector.UPDATE_DIRECTOR.getMessage() + updatedDirector);
        return updatedDirector;

    }

    @Override
    public Director deleteDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new ObjectNotFoundException(LogDirector.FAILED_TO_REMOVE_DIRECTOR.getMessage()
                    + LogDirector.NO_FOUND_DIRECTOR.getMessage()
            );
        }
        Director removedDirector = buildDirectorFromRow(directorRow);
        jdbcTemplate.execute(requestDeleteById + id);
        if (getAllDirectors().size() == 0) {
            jdbcTemplate.execute(requestResetPK);
        }
        log.info(LogDirector.DELETE_DIRECTOR.getMessage() + removedDirector);
        return removedDirector;
    }

    @Override
    public void deleteAllDirectors() {
        String sqlRequest = requestClearTableDirectors;
        jdbcTemplate.execute(sqlRequest);

        log.info(LogDirector.TABLE_DIRECTOR_CLEAR.getMessage());

    }

    @Override
    public Collection<Film> loadAllDirectors(Collection<Film> films) {
        Map<Integer, Film> filmsMap = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(", ", Collections.nCopies(filmsMap.size(), "?"));
        String sql = String.format(requestToLoadAllDirectors, inSql);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, filmsMap.keySet().toArray());

        while (sqlRowSet.next()) {
            Film film = filmsMap.get(sqlRowSet.getInt("film_id"));
            if (film != null) {
                film.addDirector(new Director(sqlRowSet.getInt("director_id"),
                        sqlRowSet.getString("director_name")));
            }
        }
        return films;
    }

    private boolean isPresentInDB(Director director) {
        return getIdRowsFromDB(director).next();
    }

    private boolean isPresentInDB(Integer id) {
        return getDirectorRowByID(id).next();
    }

    private SqlRowSet getDirectorRowByID(Integer id) {
        return jdbcTemplate.queryForRowSet(requestGetDirectorById, id);
    }

    private SqlRowSet getIdRowsFromDB(Director director) {
        return jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName());
    }

}