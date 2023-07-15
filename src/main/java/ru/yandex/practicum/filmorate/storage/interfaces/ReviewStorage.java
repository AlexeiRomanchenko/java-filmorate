package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

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
