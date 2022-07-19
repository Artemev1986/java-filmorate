package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    public Review addReview (Review review);
    public Review updateReview (Review review);
    public void deleteReviewById(long id);
    public Review getReviewById(long id);
    public List<Review> getReviewsByFilmId(long filmId, int count);
    public List<Review> getReviews(int count);
}
