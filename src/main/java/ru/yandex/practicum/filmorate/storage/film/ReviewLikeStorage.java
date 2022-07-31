package ru.yandex.practicum.filmorate.storage.film;

public interface ReviewLikeStorage {
    void addLikeDislikeForReview(long reviewId, long userId, boolean isUseful);
    void deleteLikeDislikeForReview(long reviewId, long userId);
}
