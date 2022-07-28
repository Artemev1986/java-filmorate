package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO review" +
                    "(content," +
                    " is_positive," +
                    " user_id," +
                    " film_id)" +
                    " VALUES (?, ?, ?, ?);", new String[] {"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update("UPDATE review " +
                        "SET content = ?, " +
                        "is_positive = ? " +
                        "WHERE review_id = ?;",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public void deleteReviewById(long id) {
        jdbcTemplate.update("DELETE FROM review WHERE review_id = ?;", id);
    }

    @Override
    public Optional<Review> getReviewById(long id) {
        return Optional.ofNullable(jdbcTemplate.query("SELECT\n" +
                "    r.review_id AS r_id,\n" +
                "    r.content AS r_content,\n" +
                "    r.is_positive AS r_is_positive,\n" +
                "    r.user_id AS r_user_id,\n" +
                "    r.film_id AS r_film_id,\n" +
                "    (COALESCE(lik, 0) - COALESCE(dis, 0)) AS useful\n" +
                "                FROM review AS r\n" +
                "                LEFT JOIN\n" +
                "                     (SELECT review_id, COUNT(user_id) AS dis FROM review_likes\n" +
                "                      WHERE is_useful = FALSE\n" +
                "                      GROUP BY review_id ) AS d ON r.review_id = d.review_id\n" +
                "                LEFT JOIN\n" +
                "                     (SELECT review_id, COUNT(user_id) AS lik FROM review_likes\n" +
                "                      WHERE is_useful = TRUE\n" +
                "                      GROUP BY review_id ) AS l ON r.review_id = l.review_id\n" +
                "WHERE r.review_id = ?;", (rs) -> {
            if (rs.next()) {
                return makeReview(rs);
            }
            return null;
            }, id));
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        return jdbcTemplate.query("SELECT\n" +
                "    r.review_id AS r_id,\n" +
                "    r.content AS r_content,\n" +
                "    r.is_positive AS r_is_positive,\n" +
                "    r.user_id AS r_user_id,\n" +
                "    r.film_id AS r_film_id,\n" +
                "    (COALESCE(lik, 0) - COALESCE(dis, 0)) AS useful\n" +
                "FROM review AS r\n" +
                "         LEFT JOIN\n" +
                "     (SELECT review_id, COUNT(user_id) AS dis FROM review_likes\n" +
                "      WHERE is_useful = FALSE\n" +
                "      GROUP BY review_id ) AS d ON r.review_id = d.review_id\n" +
                "         LEFT JOIN\n" +
                "     (SELECT review_id, COUNT(user_id) AS lik FROM review_likes\n" +
                "      WHERE is_useful = TRUE\n" +
                "      GROUP BY review_id ) AS l ON r.review_id = l.review_id\n" +
                "WHERE r.film_id = ?\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?;", (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public List<Review> getReviews(int count) {
        return jdbcTemplate.query("SELECT\n" +
                "    r.review_id AS r_id,\n" +
                "    r.content AS r_content,\n" +
                "    r.is_positive AS r_is_positive,\n" +
                "    r.user_id AS r_user_id,\n" +
                "    r.film_id AS r_film_id,\n" +
                "    (COALESCE(lik, 0) - COALESCE(dis, 0)) AS useful\n" +
                "FROM review AS r\n" +
                "         LEFT JOIN\n" +
                "     (SELECT review_id, COUNT(user_id) AS dis FROM review_likes\n" +
                "      WHERE is_useful = FALSE\n" +
                "      GROUP BY review_id ) AS d ON r.review_id = d.review_id\n" +
                "         LEFT JOIN\n" +
                "     (SELECT review_id, COUNT(user_id) AS lik FROM review_likes\n" +
                "      WHERE is_useful = TRUE\n" +
                "      GROUP BY review_id ) AS l ON r.review_id = l.review_id\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?;", (rs, rowNum) -> makeReview(rs), count);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("r_id"));
        review.setContent(rs.getString("r_content"));
        review.setIsPositive(rs.getBoolean("r_is_positive"));
        review.setUserId(rs.getLong("r_user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getLong("useful"));
        return review;
    }
}
