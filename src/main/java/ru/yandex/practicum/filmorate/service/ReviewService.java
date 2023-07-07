package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesReviews;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserService userService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
    }

    public Review create(Review review) {
        validationBeforeCreate(review);
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        validationBeforeUpdate(review);
        Review newData = reviewStorage.update(review);
        if (newData == null) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + review.getReviewId());
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + review.getReviewId());
        }

        return newData;
    }

    public void validationBeforeUpdate(Review review) {
        validateId(review.getReviewId());
    }

    public void validateId(Integer id) {
        if (id == null) {
            log.warn(LogMessagesReviews.MSG_ERR_ID.getMessage());
            throw new BadRequestException(LogMessagesReviews.MSG_ERR_ID.getMessage());
        }
        if (id < 0) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
        }
    }

    public void delete(Integer id) {
        validateId(id);
        if (reviewStorage.delete(id) == 0) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
        }
    }

    public List<Review> findAll() {
        List<Review> reviews = reviewStorage.findAll();
        reviewStorage.loadAllGrades(reviews);
        reviews.sort(Comparator.comparing(Review::getUseful).reversed());
        return reviews;
    }

    public Review findById(Integer id) {
        Review review = reviewStorage.findById(id).orElseThrow(() -> {
            String message = "Отзыв не найден";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        });
        reviewStorage.loadGrades(review);
        return review;
    }

    public void validationBeforeCreate(Review review) {
        validateId(review.getFilmId());
        validateId(review.getUserId());
        if (review.getIsPositive() == null) {
            throw new ValidationException("review isPositive не заполнено!");
        }
    }

    public void addLike(Integer id, Integer userId) {
        addGrade(id, userId, true);
    }

    public void addDislike(Integer id, Integer userId) {
        addGrade(id, userId, false);
    }

    public void delLike(Integer id, Integer userId) {
        delGrade(id, userId);
    }

    private void addGrade(Integer id, Integer userId, boolean positive) {
        Review review = this.findById(id);
        User user = userService.findUser(userId);
        validateForGrade(review, user);
        review.addGrade(userId, positive);
        reviewStorage.saveGrades(review);
    }

    private void delGrade(Integer id, Integer userId) {
        Review review = this.findById(id);
        User user = userService.findUser(userId);
        validateForGrade(review, user);
        review.delGrade(userId);
        reviewStorage.saveGrades(review);
    }

    private void validateForGrade(Review review, User user) {
        if (review == null) {
            String message = "Отзыв не найден";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        if (user == null) {
            String message = ("Пользователь не найден");
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public List<Review> findAllByFilm(Integer filmId, Integer count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = reviewStorage.findAll();
        } else {
            reviews = reviewStorage.findAllByFilm(filmId);
        }
        reviewStorage.loadAllGrades(reviews);

        return reviews.stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
