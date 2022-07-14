package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    private String name;
    @NotEmpty(message = "Email cannot be empty.")
    @Email(message = "Enter a valid email address.")
    private String email;
    @NotBlank(message = "Login cannot be blank.")
    private String login;
    @NotNull(message = "Birthday cannot be null.")
    @PastOrPresent(message = "Birthday cannot be in the future")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
