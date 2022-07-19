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
    public void addLikeForReview(long reviewId, long userId) {

    }

    @Override
    public void addDislikeForReview(long reviewId, long userId) {

    }

    @Override
    public void deleteLikeForReview(long reviewId, long userId) {

    }

    @Override
    public void deleteDislikeForReview(long reviewId, long userId) {

    }
}
