package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    @Order(1)
    void getAllGenre() {
        assertThat(genreDbStorage.getAllGenre().size()).isEqualTo(6);
    }

    @Test
    @Order(2)
    void getGenreById() {
        assertThat(genreDbStorage.getGenreById(3)).isPresent()
                .hasValueSatisfying(g -> assertThat(g)
                        .hasFieldOrPropertyWithValue("name", "Мультфильм")
                );
    }
}