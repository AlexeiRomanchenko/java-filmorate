package ru.yandex.practicum.filmorate.storage.db.mapper;

import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper {
    public static Review mapToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        Integer id = resultSet.getInt("REVIEW_ID");
        review.setReviewId(id);
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        return review;
    }
}
