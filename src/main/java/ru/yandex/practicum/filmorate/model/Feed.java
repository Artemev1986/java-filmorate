package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Feed {
    private long timestamp;
    @NotNull(message = "UserId cannot be null.")
    private long userId;
    @NotBlank(message = "EventType cannot be blank.")
    private String eventType;
    @NotBlank(message = "Operation cannot be blank.")
    private String operation;
    private long eventId;
    @NotNull(message = "EntityId cannot be null.")
    private long entityId;
}