package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.description.LogMessagesReviews;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Integer id) {
        log.info(LogMessagesReviews.GET_REVIEW_BY_ID_REQUEST.getMessage() + id);
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> findAll() {
        log.info(LogMessagesReviews.GET_ALL_REVIEWS_REQUEST.getMessage());
        return reviewService.findAll();
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info(LogMessagesReviews.CREATE_REVIEW_REQUEST.getMessage());
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info(LogMessagesReviews.UPDATE_REVIEW_REQUEST.getMessage());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info(LogMessagesReviews.DELETE_REVIEW_REQUEST.getMessage());
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(LogMessagesReviews.UPDATE_LIKE_REQUEST.getMessage());
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(LogMessagesReviews.UPDATE_DISLIKE_REQUEST.getMessage());
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void delLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(LogMessagesReviews.DELETE_LIKE_REQUEST.getMessage());
        reviewService.delLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void delDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(LogMessagesReviews.DELETE_DISLIKE_REQUEST.getMessage());
        reviewService.delLike(id, userId);
    }

    @GetMapping(params = {"filmId"})
    public List<Review> findAllByFilm(@RequestParam(required = false) Integer filmId,
                                      @RequestParam(defaultValue = "10") Integer count) {
        log.info(LogMessagesReviews.GET_REVIEWS_BY_FILM_REQUEST.getMessage());
        return reviewService.findAllByFilm(filmId, count);
    }
}
