package ru.yandex.practicum.filmorate.storage.film;

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
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

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
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @Order(1)
    void addFilm() {
        Film film = new Film();
        film.setName("film1");
        film.setDescription("description1");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2000", formatter));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmDbStorage.addFilm(film);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", film.getId())
                                .hasFieldOrPropertyWithValue("name", "film1")
                                .hasFieldOrPropertyWithValue("description", "description1")
                                .hasFieldOrPropertyWithValue("duration", 200L)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("13.04.2000", formatter))
                );
    }

    @Test
    @Order(2)
    void updateFilm() {
        Film film = filmDbStorage.getFilmById(1).orElse(new Film());
        film.setDuration(100);
        filmDbStorage.updateFilm(film);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("duration", 100L)
                );
    }

    @Test
    @Order(3)
    void findAllFilms() {
        Film film = new Film();
        film.setName("film2");
        film.setDescription("description2");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.parse("13.04.2008", formatter));
        Mpa mpa = new Mpa();
        mpa.setId(2);
        film.setMpa(mpa);
        filmDbStorage.addFilm(film);

        List<Film> films = filmDbStorage.findAllFilms();
        Optional<Film> filmOptional = Optional.of(films.get(0));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "film1")
                );

        filmOptional = Optional.of(films.get(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "film2")
                );

    }

    @Test
    @Order(4)
    void getFilmById() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "film1")
                                .hasFieldOrPropertyWithValue("description", "description1")
                                .hasFieldOrPropertyWithValue("duration", 100L)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("13.04.2000", formatter))
                );
    }

    @Test
    @Order(5)
    void deleteFilmById() {
        filmDbStorage.deleteFilmById(1);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional.orElse(null)).isNull();
    }

    @Test
    @Order(6)
    void addLike() {
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
        userStorage.addUser(user);
        filmDbStorage.addLike(2L, 1L);

        Optional<Film> filmOptional = filmDbStorage.getFilmById(2);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("likes", Set.copyOf(List.of(1L)))
                );
    }

    @Test
    @Order(7)
    void deleteLike() {
        filmDbStorage.deleteLike(2L, 1L);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(2);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("likes", new HashSet<>())
                );
    }

    @Test
    @Order(8)
    void getPopularFilms() {
        Film film = new Film();
        film.setName("film3");
        film.setDescription("description3");
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

        filmDbStorage.addLike(2L, 1L);
        filmDbStorage.addLike(3L, 2L);
        filmDbStorage.addLike(3L, 3L);

        List<Film> films = filmDbStorage.getPopularFilms(1);

        Optional<Film> filmOptional = Optional.of(films.get(0));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("likes", Set.copyOf(List.of(2L, 3L)))
                );
    }

    @Test
    @Order(9)
    void getAllMpa() {
        assertThat(filmDbStorage.getAllMpa().size()).isEqualTo(5);
    }

    @Test
    @Order(10)
    void getMPAById() {
        assertThat(filmDbStorage.getMpaById(2)).isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa)
                                .hasFieldOrPropertyWithValue("name", "PG")
                );
    }

    @Test
    @Order(11)
    void getAllGenre() {
        assertThat(filmDbStorage.getAllGenre().size()).isEqualTo(6);
    }

    @Test
    @Order(12)
    void getGenreById() {
        assertThat(filmDbStorage.getGenreById(3)).isPresent()
                .hasValueSatisfying(g -> assertThat(g)
                        .hasFieldOrPropertyWithValue("name", "Мультфильм")
                );
    }
}