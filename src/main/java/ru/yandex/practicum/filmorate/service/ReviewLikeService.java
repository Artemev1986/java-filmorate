package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.ReviewLikeStorage;

@Service
public class ReviewLikeService {
    private final ReviewLikeStorage reviewLikeStorage;

    @Autowired
    public ReviewLikeService(ReviewLikeStorage reviewLikeStorage) {
        this.reviewLikeStorage = reviewLikeStorage;
    }

    public void addLikeForReview (long reviewId, long userId) {
        reviewLikeStorage.addLikeForReview(reviewId, userId);
    }
    public void addDislikeForReview (long reviewId, long userId) {
        reviewLikeStorage.addDislikeForReview(reviewId, userId);
    }
    public void deleteLikeForReview (long reviewId, long userId) {
        reviewLikeStorage.deleteLikeForReview(reviewId, userId);
    }
    public void deleteDislikeForReview (long reviewId, long userId) {
        reviewLikeStorage.deleteDislikeForReview(reviewId, userId);
    }
}
