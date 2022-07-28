package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, ReviewLikeStorage reviewLikeStorage, FilmStorage filmStorage,
                         UserStorage userStorage, FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewLikeStorage = reviewLikeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public Review addReview (Review review) {
        userStorage.getUserById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id (" + review.getUserId() + ") not found"));
        filmStorage.getFilmById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film with id (" + review.getFilmId() + ") not found"));
        reviewStorage.addReview(review);
        feedStorage.addInFeed(review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        log.debug("The review with id {} added", review.getReviewId());
        return review;
    }

    public Review updateReview (Review review) {
        userStorage.getUserById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id (" + review.getUserId() + ") not found"));
        filmStorage.getFilmById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film with id (" + review.getFilmId() + ") not found"));
        getReviewById(review.getReviewId());
        reviewStorage.updateReview(review);
        feedStorage.addInFeed(getReviewById(review.getReviewId()).getUserId(),
                "REVIEW", "UPDATE", review.getReviewId());
        Review reviewFromStorage = getReviewById(review.getReviewId());
        log.debug("The review with id {} updated", review.getReviewId());
        return reviewFromStorage;
    }

    public void deleteReviewById(long id) {
        feedStorage.addInFeed(getReviewById(id).getUserId(), "REVIEW", "REMOVE", id);
        reviewStorage.deleteReviewById(id);
        log.debug("The review with id {} deleted", id);
    }

    public Review getReviewById(long id) {
        Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Review with id (" + id + ") not found"));
        log.debug("Get review by id: {}", id);
        return review;
    }

    public List<Review> getReviewsByFilmId(long filmId, int count) {
        List<Review> reviews = reviewStorage.getReviewsByFilmId(filmId, count);
        log.debug("Get {} reviews by film id {}", reviews.size(), filmId);
        return reviews;
    }

    public List<Review> getReviews(int count) {
        List<Review> reviews = reviewStorage.getReviews(count);
        log.debug("Get {} reviews", reviews.size());
        return reviews;
    }

    public void addLikeForReview (long reviewId, long userId) {
        reviewLikeStorage.addLikeDislikeForReview(reviewId, userId, true);
        log.debug("Like for the review with id {} added by user with id {}", reviewId, userId);
    }
    public void addDislikeForReview (long reviewId, long userId) {
        reviewLikeStorage.addLikeDislikeForReview(reviewId, userId, false);
        log.debug("Dislike for the review with id {} added by user with id {}", reviewId, userId);
    }
    public void deleteLikeForReview (long reviewId, long userId) {
        reviewLikeStorage.deleteLikeDislikeForReview(reviewId, userId);
        log.debug("Like for the review with id {} deleted by user with id {}", reviewId, userId);
    }
    public void deleteDislikeForReview (long reviewId, long userId) {
        reviewLikeStorage.deleteLikeDislikeForReview(reviewId, userId);
        log.debug("Dislike for the review with id {} deleted by user with id {}", reviewId, userId);
    }
}
