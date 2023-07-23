package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Review> findById(Integer id) {
        String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        List<Review> result = jdbcTemplate.query(sql, ReviewMapper::mapToReview, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Review review = result.get(0);

        String sqlUseful = "SELECT * FROM REVIEW_USEFUL WHERE  REVIEW_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlUseful, review.getReviewId());
        while (sqlRowSet.next()) {
            review.setUseful(review.getUseful() + getGrade(sqlRowSet.getBoolean("GRADE")));
        }
        return Optional.of(review);
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS";

        List<Review> reviews = jdbcTemplate.query(sql, ReviewMapper::mapToReview);
        Map<Integer, Review> reviewMap = reviews.stream().collect(Collectors.toMap(Review::getReviewId, Function.identity()));

        String sqlUseful = "SELECT * FROM REVIEW_USEFUL";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlUseful);

        while (sqlRowSet.next()) {
            Review review = reviewMap.get(sqlRowSet.getInt("REVIEW_ID"));
            if (review != null) {
                review.setUseful(review.getUseful() + getGrade(sqlRowSet.getBoolean("GRADE")));
            }
        }

        return reviews;
    }

    @Override
    public List<Review> findAllByFilm(Integer filmId) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";

        List<Review> reviews = jdbcTemplate.query(sql, ReviewMapper::mapToReview, filmId);
        Map<Integer, Review> reviewMap = reviews.stream().collect(Collectors.toMap(Review::getReviewId, Function.identity()));

        String sqlUseful = "SELECT REVIEW_USEFUL.REVIEW_ID, REVIEW_USEFUL.USER_ID, REVIEW_USEFUL.GRADE FROM REVIEW_USEFUL " +
                "JOIN REVIEWS ON REVIEW_USEFUL.REVIEW_ID = REVIEWS.REVIEW_ID " +
                "WHERE  REVIEWS.FILM_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlUseful, filmId);
        while (sqlRowSet.next()) {
            Review review = reviewMap.get(sqlRowSet.getInt("REVIEW_ID"));
            if (review != null) {
                review.setUseful(review.getUseful() + getGrade(sqlRowSet.getBoolean("GRADE")));
            }
        }
        return reviews;
    }

    @Override
    public Review create(Review review) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("reviews")
                .usingColumns("content", "is_positive", "user_id", "film_id")
                .usingGeneratedKeyColumns("review_id")
                .executeAndReturnKeyHolder(Map.of(
                        "is_positive", review.getIsPositive(),
                        "content", review.getContent(),
                        "user_id", review.getUserId(),
                        "film_id", review.getFilmId()))
                .getKeys();
        assert keys != null;
        review.setReviewId((Integer) keys.get("review_id"));
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        return findById(review.getReviewId()).orElseThrow(() -> {
            String message = "Отзыв не найден";
            return new ObjectNotFoundException(message);
        });
    }

    @Override
    public int delete(Integer id) {
        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ?", id);
        return jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ?", id);
    }

    public Map<Integer, Boolean> addGrade(Integer id, Integer userId, boolean positive) {
        String sqlUseful = "SELECT * FROM REVIEW_USEFUL WHERE  REVIEW_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlUseful, id);
        Map<Integer, Boolean> grades = new HashMap<>();
        while (sqlRowSet.next()) {
            grades.put(sqlRowSet.getInt("USER_ID"), sqlRowSet.getBoolean("GRADE"));
        }
        grades.put(userId, positive);
        return grades;
    }

    public void delGrade(Integer id, Integer userId) {
        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ?" +
                "               AND USER_ID = ?", id, userId);
    }

    @Override
    public void saveGrades(Review review, Map<Integer, Boolean> grades) {
        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ?", review.getReviewId());

        String sql = "INSERT INTO REVIEW_USEFUL (REVIEW_ID, USER_ID, GRADE) VALUES(?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (var grade : grades.entrySet()) {
            Object[] args = {review.getReviewId(), grade.getKey(), grade.getValue()};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private Integer getGrade(boolean grade) {
        if (grade) {
            return 1;
        } else {
            return -1;
        }
    }
}
