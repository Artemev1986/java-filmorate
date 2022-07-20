package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.ReviewLikeStorage;

@Component
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeDislikeForReview(long reviewId, long userId, boolean isUseful) {
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, is_useful) VALUES (?, ?, ?);",
                reviewId, userId, isUseful);
    }

    @Override
    public void deleteLikeDislikeForReview(long reviewId, long userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?;", reviewId, userId);
    }
}
