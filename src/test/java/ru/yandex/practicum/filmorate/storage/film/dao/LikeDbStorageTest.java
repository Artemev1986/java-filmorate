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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LikeDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @Test
    @Order(1)
    void addLike() {
        Film film = new Film();
        film.setName("film1");
        film.setDescription("description1");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2000", formatter));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmDbStorage.addFilm(film);
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
        userStorage.addUser(user);
        likeDbStorage.addLike(1L, 1L);

        assertThat(likeDbStorage.getLikesByFilmId(1L)).isEqualTo(Set.copyOf(List.of(1L)));
    }

    @Test
    @Order(2)
    void deleteLike() {
        likeDbStorage.deleteLike(1L, 1L);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("likes", new HashSet<>())
                );
    }

    @Test
    @Order(3)
    void getPopularFilms() {
        Film film = new Film();
        film.setName("film2");
        film.setDescription("description2");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2008", formatter));
        Mpa mpa = new Mpa();
        mpa.setId(2);
        film.setMpa(mpa);
        filmDbStorage.addFilm(film);

        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        user.setLogin("alex");
        user.setBirthday(LocalDate.parse("13.04.1980", formatter));
        userStorage.addUser(user);

        user.setName("Oleg");
        user.setEmail("oleg@gmail.com");
        user.setLogin("oleg");
        user.setBirthday(LocalDate.parse("13.04.1970", formatter));
        userStorage.addUser(user);

        likeDbStorage.addLike(1L, 1L);
        likeDbStorage.addLike(2L, 2L);
        likeDbStorage.addLike(2L, 3L);

        List<Film> films = filmDbStorage.getPopularFilms(1,null,null);

        Optional<Film> filmOptional = Optional.of(films.get(0));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", 2L)
                );
    }
}