package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
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

    @PutMapping("/films/{id}/like/{userId}")
    public long addLike(@Valid @PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        return userId;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public long deleteLike(@Valid @PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
        return userId;
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") long count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getGenre() {
        return filmService.getAllGenre();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@Valid @PathVariable int id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@Valid @PathVariable int id) {
        return filmService.getMpaById(id);
    }
}
