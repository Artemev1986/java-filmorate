package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private long reviewId;
    @NotBlank(message = "Content cannot be blank.")
    private String content;
    @NotNull(message = "Review type cannot be null.")
    private Boolean isPositive;
    @NotNull(message = "userId cannot be null.")
    private Long userId;
    @NotNull(message = "filmId cannot be null.")
    private Long filmId;
    private long useful;
}
