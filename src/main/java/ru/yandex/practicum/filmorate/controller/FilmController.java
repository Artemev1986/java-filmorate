package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping()
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@Valid @PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/films/{id}")
    public long deleteFilmById(@Valid @PathVariable long id) {
        filmService.deleteFilmById(id);
        return id;
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") long count) {
        return filmService.getPopularFilms(count);
    }
}
