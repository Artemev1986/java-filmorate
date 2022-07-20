package ru.yandex.practicum.filmorate.storage.film;

public interface ReviewLikeStorage {
    public void addLikeDislikeForReview(long reviewId, long userId, boolean isUseful);
    public void deleteLikeDislikeForReview(long reviewId, long userId);
}
