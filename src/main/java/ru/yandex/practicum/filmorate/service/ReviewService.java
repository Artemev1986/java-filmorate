package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmStorage filmStorage, UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review addReview (Review review) {
        userStorage.getUserById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id (" + review.getUserId() + ") not found"));
        filmStorage.getFilmById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film with id (" + review.getFilmId() + ") not found"));
        reviewStorage.addReview(review);
        log.debug("The review with id {} added", review.getReviewId());
        return review;
    }

    public Review updateReview (Review review) {
        userStorage.getUserById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id (" + review.getUserId() + ") not found"));
        filmStorage.getFilmById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film with id (" + review.getFilmId() + ") not found"));
        reviewStorage.updateReview(review);
        log.debug("The review with id {} updated", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    public void deleteReviewById(long id) {
        getReviewById(id); //Will throw an exception if there is no review with id
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
}
