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
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    @Order(1)
    void getAllMpa() {
        assertThat(mpaDbStorage.getAllMpa().size()).isEqualTo(5);
    }

    @Test
    @Order(2)
    void getMPAById() {
        assertThat(mpaDbStorage.getMpaById(2)).isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa)
                        .hasFieldOrPropertyWithValue("name", "PG")
                );
    }

}