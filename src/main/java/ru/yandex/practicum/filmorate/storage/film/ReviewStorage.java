package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review addReview (Review review);
    Review updateReview (Review review);
    void deleteReviewById(long id);
    Optional<Review> getReviewById(long id);
    List<Review> getReviewsByFilmId(long filmId, int count);
    List<Review> getReviews(int count);
}
