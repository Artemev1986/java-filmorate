package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.ReviewLikeStorage;

@Slf4j
@Service
public class ReviewLikeService {
    private final ReviewLikeStorage reviewLikeStorage;

    @Autowired
    public ReviewLikeService(ReviewLikeStorage reviewLikeStorage) {
        this.reviewLikeStorage = reviewLikeStorage;
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
