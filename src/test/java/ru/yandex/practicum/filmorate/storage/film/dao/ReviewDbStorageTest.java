package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @Order(1)
    void addReview() {
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
        userDbStorage.addUser(user);

        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        user.setLogin("Al");
        user.setBirthday(LocalDate.parse("11.01.1981", formatter));
        userDbStorage.addUser(user);

        Film film = new Film();
        film.setName("film1");
        film.setDescription("description1");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2000", formatter));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmDbStorage.addFilm(film);

        film.setName("film2");
        film.setDescription("description2");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2008", formatter));
        mpa.setId(2);
        film.setMpa(mpa);
        filmDbStorage.addFilm(film);

        Review review = new Review();
        review.setContent("film review 1");
        review.setIsPositive(false);
        review.setFilmId(1L);
        review.setUserId(1L);
        reviewDbStorage.addReview(review);

        Optional<Review> reviewOptional = reviewDbStorage.getReviewById(1L);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r)
                        .hasFieldOrPropertyWithValue("reviewId", 1L)
                        .hasFieldOrPropertyWithValue("content", "film review 1")
                        .hasFieldOrPropertyWithValue("isPositive", false)
                        .hasFieldOrPropertyWithValue("filmId", 1L)
                        .hasFieldOrPropertyWithValue("userId", 1L)
                );
    }

    @Test
    @Order(2)
    void updateReview() {
        Review review = new Review();
        review.setReviewId(1);
        review.setContent("film review 1 edited");
        review.setIsPositive(true);
        review.setFilmId(2L);
        review.setUserId(2L);
        review.setUseful(10L);
        reviewDbStorage.updateReview(review);

        Optional<Review> reviewOptional = reviewDbStorage.getReviewById(1L);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r)
                        .hasFieldOrPropertyWithValue("reviewId", 1L)
                        .hasFieldOrPropertyWithValue("content", "film review 1 edited")
                        .hasFieldOrPropertyWithValue("isPositive", true)
                        .hasFieldOrPropertyWithValue("filmId", 1L)
                        .hasFieldOrPropertyWithValue("userId", 1L)
                        .hasFieldOrPropertyWithValue("useful", 0L)
                );
    }

    @Test
    @Order(3)
    void getReviewsByFilmId() {
        Review review = new Review();
        review.setContent("film review 2");
        review.setIsPositive(true);
        review.setFilmId(1L);
        review.setUserId(2L);
        reviewDbStorage.addReview(review);

        review.setContent("film review 3");
        review.setIsPositive(false);
        review.setFilmId(2L);
        review.setUserId(2L);
        reviewDbStorage.addReview(review);

        reviewLikeDbStorage.addLikeDislikeForReview(1, 1, true);
        reviewLikeDbStorage.addLikeDislikeForReview(1, 2, true);
        reviewLikeDbStorage.addLikeDislikeForReview(2, 1, false);
        reviewLikeDbStorage.addLikeDislikeForReview(2, 2, false);
        reviewLikeDbStorage.addLikeDislikeForReview(3, 1, true);
        reviewLikeDbStorage.addLikeDislikeForReview(3, 2, false);

        List<Review> reviews = reviewDbStorage.getReviewsByFilmId(1, 3);

        assertThat(reviews.size()).isEqualTo(2);

        assertThat(reviews.get(0)).hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "film review 1 edited")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("useful", 2L);

        assertThat(reviews.get(1)).hasFieldOrPropertyWithValue("reviewId", 2L)
                .hasFieldOrPropertyWithValue("content", "film review 2")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 2L)
                .hasFieldOrPropertyWithValue("useful", -2L);
    }

    @Test
    @Order(4)
    void getReviews() {
        List<Review> reviews = reviewDbStorage.getReviews( 3);

        assertThat(reviews.size()).isEqualTo(3);

        assertThat(reviews.get(0)).hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "film review 1 edited")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("useful", 2L);

        assertThat(reviews.get(1)).hasFieldOrPropertyWithValue("reviewId", 3L)
                .hasFieldOrPropertyWithValue("content", "film review 3")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("filmId", 2L)
                .hasFieldOrPropertyWithValue("userId", 2L)
                .hasFieldOrPropertyWithValue("useful", 0L);

        assertThat(reviews.get(2)).hasFieldOrPropertyWithValue("reviewId", 2L)
                .hasFieldOrPropertyWithValue("content", "film review 2")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 2L)
                .hasFieldOrPropertyWithValue("useful", -2L);
    }

    @Test
    @Order(5)
    void deleteReviewById() {
        reviewDbStorage.deleteReviewById(1);
        List<Review> reviews = reviewDbStorage.getReviews( 3);
        assertThat(reviews.size()).isEqualTo(2);
    }
}