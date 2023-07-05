package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review findById(Integer id);

    List<Review> findAll();

    List<Review> findAllByFilm(Integer filmId);

    Review create(Review review);

    Review update(Review review);

    void delete(Integer id);

    void loadGrades(Review review);

    void saveGrades(Review review);
}
