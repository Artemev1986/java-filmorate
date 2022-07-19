package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Review {
    private long reviewId;
    @NotBlank(message = "Content cannot be blank.")
    private String content;
    @NotNull(message = "Review type cannot be null.")
    private Boolean isPositive;
    @Min(value = 1, message = "userId must be at least 1.")
    @NotNull(message = "userId cannot be null.")
    private Long userId;
    @Min(value = 1, message = "filmId must be at least 1.")
    @NotNull(message = "filmId cannot be null.")
    private Long filmId;
    private long useful;
}
