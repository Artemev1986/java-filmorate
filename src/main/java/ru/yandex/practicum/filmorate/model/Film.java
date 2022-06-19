package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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

    public void addLike(long id) {
        likes.add(id);
    }
    public void deleteLike(long id) {
        if (likes != null) {
            likes.remove(id);
        }
    }

}
