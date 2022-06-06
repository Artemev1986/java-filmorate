package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource(value = {
            "1,Titanic,description,200,13.04.2000",
            "2,It,description,220,13.04.1996",
    }, ignoreLeadingAndTrailingWhitespace = false)
    void whenValidInputThenReturns200(int id,
                                       String name,
                                       String description,
                                       long duration,
                                       String releaseDate) throws Exception {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setReleaseDate(LocalDate.parse(releaseDate, formatter));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, ,description,200,13.04.2000",
            "1,Titanic,ssssssssssssssssssssssssssssssssssssssssssssssssss" +
                    "ssssssssssssssssssssssssssssssssssssssssssssssssss" +
                    "ssssssssssssssssssssssssssssssssssssssssssssssssss" +
                    "sssssssssssssssssssssssssssssssssssssssssssssssssss,200,13.04.2000",
            "1,Titanic,description,200,27.12.1895",
            "1,Titanic,description,-1,13.04.2000"
    }, ignoreLeadingAndTrailingWhitespace = false)
    void whenInvalidInputThenReturns400(int id,
                                         String name,
                                         String description,
                                         long duration,
                                         String releaseDate) throws Exception {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setReleaseDate(LocalDate.parse(releaseDate, formatter));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-1,Film,description,200,13.04.2000"})
    void whenInvalidIdThenReturns500(int id,
                                         String name,
                                         String description,
                                         long duration,
                                         String releaseDate) throws Exception {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setReleaseDate(LocalDate.parse(releaseDate, formatter));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isInternalServerError());
    }
}