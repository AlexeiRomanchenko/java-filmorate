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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS";
        return jdbcTemplate.query(sql, ReviewMapper::mapToReview);
    }

    @Override
    public List<Review> findAllByFilm(Integer filmId) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, ReviewMapper::mapToReview, filmId);
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

    @Override
    public void loadAllGrades(List<Review> reviews) {
        String sql = "SELECT * FROM REVIEW_USEFUL";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

        Map<Integer, Review> reviewMap = reviews.stream().collect(Collectors.toMap(Review::getReviewId, Function.identity()));

        while (sqlRowSet.next()) {
            Review review = reviewMap.get(sqlRowSet.getInt("REVIEW_ID"));
            if (review != null) {
                review.addGrade(sqlRowSet.getInt("USER_ID"), sqlRowSet.getBoolean("GRADE"));
            }
        }
    }

    @Override
    public void loadGrades(Review review) {
        String sql = "SELECT * FROM REVIEW_USEFUL WHERE  REVIEW_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, review.getReviewId());
        while (sqlRowSet.next()) {
            review.addGrade(sqlRowSet.getInt("USER_ID"), sqlRowSet.getBoolean("GRADE"));
        }
    }

    @Override
    public void saveGrades(Review review) {
        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ?", review.getReviewId());

        String sql = "INSERT INTO REVIEW_USEFUL (REVIEW_ID, USER_ID, GRADE) VALUES(?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        Map<Integer, Boolean> grades = review.getGrades();
        for (var grade : grades.entrySet()) {
            Object[] args = {review.getReviewId(), grade.getKey(), grade.getValue()};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
