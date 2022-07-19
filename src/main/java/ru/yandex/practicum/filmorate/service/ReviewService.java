package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review addReview (Review review) {
        return reviewStorage.addReview(review);
    }
    public Review updateReview (Review review) {
        return reviewStorage.updateReview(review);
    }
    public void deleteReviewById(long id) {
        reviewStorage.deleteReviewById(id);
    }
    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }
    public List<Review> getReviews(int count) {
        return reviewStorage.getReviews(count);
    }
}
