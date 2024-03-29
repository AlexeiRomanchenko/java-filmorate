package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.EventType;
import ru.yandex.practicum.filmorate.description.LogMessagesReviews;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.description.Operation;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public Review create(Review review) {
        validationBeforeCreate(review);
        Review newData = reviewStorage.create(review);
        eventStorage.createEvent(newData.getUserId(), EventType.REVIEW, Operation.ADD, newData.getReviewId());
        return newData;
    }

    public Review update(Review review) {
        validationBeforeUpdate(review);
        Review newData = reviewStorage.update(review);
        if (newData == null) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage(), review.getReviewId());
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + review.getReviewId());
        }
        eventStorage.createEvent(newData.getUserId(), EventType.REVIEW, Operation.UPDATE, newData.getReviewId());
        return newData;
    }

    private void validationBeforeUpdate(Review review) {
        validateId(review.getReviewId());
    }

    private void validateId(Integer id) {
        if (id < 0) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage(), id);
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
        }
    }

    public void delete(Integer id) {
        Review review = this.findById(id);
        eventStorage.createEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId());
        if (reviewStorage.delete(id) == 0) {
            log.warn(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage(), id);
            throw new ObjectNotFoundException(LogMessagesReviews.MSG_ERR_NOT_FOUND.getMessage() + id);
        }
    }

    public List<Review> findAll() {
        List<Review> reviews = reviewStorage.findAll();
        Comparator<Review> usefulComparator = Comparator.comparing(Review::getUseful).reversed();
        reviews.sort(usefulComparator);
        return reviews;
    }

    public Review findById(Integer id) {
        return reviewStorage.findById(id).orElseThrow(() -> {
            String message = "Отзыв не найден";
            log.warn(message);
            return new ObjectNotFoundException(message);
        });
    }

    private void validationBeforeCreate(Review review) {
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
        Review review = this.findById(id);
        User user = userStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID + userId.toString()));
        validateForGrade(review, user);
        reviewStorage.delGrade(id, userId);
    }

    private void addGrade(Integer id, Integer userId, boolean positive) {
        Review review = this.findById(id);
        User user = userStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException(LogMessagesUsers.USER_NO_FOUND_WITH_ID + userId.toString()));
        validateForGrade(review, user);
        reviewStorage.saveGrades(review, reviewStorage.addGrade(id, userId, positive));
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

        Comparator<Review> usefulComparator = Comparator.comparing(Review::getUseful).reversed();

        return reviews.stream()
                .sorted(usefulComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
