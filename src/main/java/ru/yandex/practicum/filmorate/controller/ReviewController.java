package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    public ReviewController(ReviewService reviewService, ReviewLikeService reviewLikeService) {
        this.reviewService = reviewService;
        this.reviewLikeService = reviewLikeService;
    }

    @PostMapping()
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping()
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@Valid @PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/{id}")
    public long deleteReviewById(@Valid @PathVariable long id) {
        reviewService.deleteReviewById(id);
        return id;
    }

    @GetMapping()
    public List<Review> getPopularFilms(@Positive @RequestParam(required = false) Long filmId,
                                        @Positive @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == null) {
            return reviewService.getReviews(count);
        } else {
            return reviewService.getReviewsByFilmId(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public long addLikeForReview(@Valid @PathVariable long id, @PathVariable long userId) {
        reviewLikeService.addLikeForReview(id, userId);
        return userId;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public long addDislikeForReview(@Valid @PathVariable long id, @PathVariable long userId) {
        reviewLikeService.addDislikeForReview(id, userId);
        return userId;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public long deleteLikeForReview(@Valid @PathVariable long id, @PathVariable long userId) {
        reviewLikeService.deleteLikeForReview(id, userId);
        return userId;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public long deleteDislikeForReview(@Valid @PathVariable long id, @PathVariable long userId) {
        reviewLikeService.deleteDislikeForReview(id, userId);
        return userId;
    }
}
