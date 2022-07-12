package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MPA {
    private Integer id;
    @NotBlank(message = "Name cannot be blank.")
    private String name;
    @NotBlank(message = "Description cannot be blank.")
    private String description;
}
