package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> findById(Integer id);

    List<Review> findAll();

    List<Review> findAllByFilm(Integer filmId);

    Review create(Review review);

    Review update(Review review);

    int delete(Integer id);

    Map<Integer, Boolean> addGrade(Integer id, Integer userId, boolean positive);

    void delGrade(Integer id, Integer userId);

    void saveGrades(Review review, Map<Integer, Boolean> grades);
}
