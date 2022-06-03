package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int id;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public Film addFilm(@RequestBody Film film) {
        try {
            if (film.getName().isEmpty() || film.getName().isBlank()) {
                throw new ValidationException("Film title is empty");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Film description is too long");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Film release date too early");
            }
            if (film.getDuration() < 0) {
                throw new ValidationException("Film duration is negative");
            }
            id++;
            film.setId(id);
            films.put(id, film);
            log.debug("Adding new film with id: {}", film.getId());
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        try {
            if (film.getName().isEmpty() || film.getName().isBlank()) {
                throw new ValidationException("Film title is empty");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Film description is too long");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Film release date too early");
            }
            if (film.getDuration() < 0) {
                throw new ValidationException("Film duration is negative");
            }
            if (film.getId() < 1) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The HTTP Status will be Internal Server Error (CODE 500)\n");
            }
            films.put(film.getId(), film);
            log.debug("Film with id {} was updated", film.getId());
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }
}
