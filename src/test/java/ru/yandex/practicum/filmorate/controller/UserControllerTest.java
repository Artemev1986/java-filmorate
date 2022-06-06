package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource(value = {
            "1,Mik,art@gmail.com,Art,13.04.1986",
            "2,,test@mail.com,Art2,13.05.1986",
            "2,Mik2,art2@gmail.com,Art2,13.05.1986"
    }, ignoreLeadingAndTrailingWhitespace = false)
    void whenValidInputThenReturns200(int id, String name, String email, String login, String date) throws Exception {
        User user = new User();
        user.setId(id);
        user.setName(Objects.requireNonNullElse(name, ""));
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(LocalDate.parse(date, formatter));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,Mikhail,,Art,13.04.1986",
            "1,Mikhail,mail.com,Art,13.04.1986",
            "1,Mikhail,test@mail.com, ,13.04.1986",
            "2,Mikhail,test@mail.com,Art2,13.05.2026"
    }, ignoreLeadingAndTrailingWhitespace = false)
    void whenInvalidInputThenReturns400(int id, String name, String email, String login, String date) throws Exception {
        User user = new User();
        user.setId(id);
        user.setName(Objects.requireNonNullElse(name, ""));
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(LocalDate.parse(date, formatter));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource(value = {"-1,Mikhail,test@mail.com,Art,13.04.1986"}, ignoreLeadingAndTrailingWhitespace = false)
    void whenInvalidIdThenReturns500(int id, String name, String email, String login, String date) throws Exception {
        User user = new User();
        user.setId(id);
        user.setName(Objects.requireNonNullElse(name, ""));
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(LocalDate.parse(date, formatter));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }
}