package ru.yandex.practicum.filmorate.storage.film;

public interface ReviewLikeStorage {
    public void addLikeForReview (long reviewId, long userId);
    public void addDislikeForReview (long reviewId, long userId);
    public void deleteLikeForReview (long reviewId, long userId);
    public void deleteDislikeForReview (long reviewId, long userId);
}
