package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int id;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
            id++;
            film.setId(id);
            films.put(id, film);
            log.debug("Adding new film with id: {}", film.getId());
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            if (film.getId() < 1) {
                throw new ValidationException("Film id less then 1");
            }
            films.put(film.getId(), film);
            log.debug("Film with id {} was updated", film.getId());
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }
}
