package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Film name cannot be blank.")
    private String name;
    @NotBlank(message = "Login cannot be blank.")
    @Size(max = 200)
    private String description;
    @NotNull(message = "Film release date cannot be null.")
    @IsAfter(minDate = "28.12.1895", message = "Film release date too early")
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private Set<Long> likes = new HashSet<>();
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Director> directors;
}
