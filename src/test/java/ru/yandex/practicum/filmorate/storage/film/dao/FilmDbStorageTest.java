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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
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
}