package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Optional<Review> findById(Integer id);

    List<Review> findAll();

    List<Review> findAllByFilm(Integer filmId);

    Review create(Review review);

    Review update(Review review);

    int delete(Integer id);

    void loadAllGrades(List<Review> reviews);

    void loadGrades(Review review);

    void saveGrades(Review review);
}
