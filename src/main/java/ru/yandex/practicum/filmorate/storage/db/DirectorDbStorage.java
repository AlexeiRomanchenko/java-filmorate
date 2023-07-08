package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.description.LogDirector;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.description.LogSQL;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirecorStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DirectorDbStorage implements DirecorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
        List<Director> directors = new ArrayList<>();
        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            Integer id = idsRow.getInt("director_id");
            directors.add(getDirectorById(id));
        }
        log.info(LogMessagesUsers.TRANSFER_LIST_ALL_USERS.getMessage());
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

        Director removedDirector = Director.builder()
                .id(directorRow.getInt("director_id"))
                .name(directorRow.getString("director_name"))
                .build();
        jdbcTemplate.execute(requestDeleteById + id);
        if (getAllDirectors().size() == 0) {
            jdbcTemplate.execute(requestResetPK);
        }
        log.info(LogDirector.DELETE_DIRECTOR.getMessage() + removedDirector);
        return removedDirector;

    }

    @Override
    public void deleteAllDirectors() {

        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            jdbcTemplate.execute(requestDeleteById + idsRow.getInt("director_id"));
        }
        jdbcTemplate.execute(requestResetPK);
        log.info(LogDirector.TABLE_DIRECTOR_CLEAR.getMessage());
    }

    private Director buildDirectorFromRow(SqlRowSet row) {
        return Director.builder()
                .name(row.getString("director_name"))
                .id(row.getInt("director_id"))
                .build();
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
        SqlRowSet rows = jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName());
        return rows;
    }

}