package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private long id;
    @NotBlank(message = "Name cannot be blank.")
    private String name;
}
