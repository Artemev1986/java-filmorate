package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    public Review addReview (Review review);
    public Review updateReview (Review review);
    public void deleteReviewById(long id);
    public Optional<Review> getReviewById(long id);
    public List<Review> getReviewsByFilmId(long filmId, int count);
    public List<Review> getReviews(int count);
}
