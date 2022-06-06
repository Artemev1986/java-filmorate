package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
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
}
