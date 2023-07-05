package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Review findById(Integer id) {
        String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        List<Review> result = jdbcTemplate.query(sql, this::mapToReview, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private Review mapToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        Integer id = resultSet.getInt("REVIEW_ID");
        review.setId(id);
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        return review;
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS";
        return jdbcTemplate.query(sql, this::mapToReview);
    }

    @Override
    public List<Review> findAllByFilm(Integer filmId) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapToReview, filmId);
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("IS_POSITIVE", review.getIsPositive());
        values.put("CONTENT", review.getContent());
        values.put("USER_ID", review.getUserId());
        values.put("FILM_ID", review.getFilmId());

        Integer id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        review.setId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? " +
                     "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getId());
        return review;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ?", id);
    }

    @Override
    public void loadGrades(Review review) {
        String sql = "SELECT * FROM REVIEW_USEFUL WHERE  REVIEW_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, review.getId());
        while (sqlRowSet.next()) {
            review.addGrade(sqlRowSet.getInt("USER_ID"), sqlRowSet.getBoolean("GRADE"));
        }
    }

    @Override
    public void saveGrades(Review review) {
        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ?", review.getId());

        String sql = "INSERT INTO REVIEW_USEFUL (REVIEW_ID, USER_ID, GRADE) VALUES(?, ?, ?)";
        Map<Integer, Boolean>  grades = review.getGrades();
        for (var grade : grades.entrySet()) {
            jdbcTemplate.update(sql, review.getId(), grade.getKey(), grade.getValue());
        }
    }
}
