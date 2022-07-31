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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewLikeDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @Order(1)
    void addLikeDislikeForReview() {
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
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

        Review review = new Review();
        review.setContent("film review 1");
        review.setIsPositive(false);
        review.setFilmId(1L);
        review.setUserId(1L);
        reviewDbStorage.addReview(review);

        reviewLikeDbStorage.addLikeDislikeForReview(1, 1, true);

        Optional<Review> reviewOptional = reviewDbStorage.getReviewById(1L);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r)
                        .hasFieldOrPropertyWithValue("useful", 1L)
                );
    }

    @Test
    @Order(2)
    void deleteLikeDislikeForReview() {
        reviewLikeDbStorage.deleteLikeDislikeForReview(1,1);

        Optional<Review> reviewOptional = reviewDbStorage.getReviewById(1L);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r)
                        .hasFieldOrPropertyWithValue("useful", 0L)
                );
    }
}