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
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping()
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@Valid @PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public long deleteFilmById(@Valid @PathVariable long id) {
        filmService.deleteFilmById(id);
        return id;
    }

    @PutMapping("/{id}/like/{userId}")
    public long addLike(@Valid @PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        return userId;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public long deleteLike(@Valid @PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
        return userId;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") long count) {
        return filmService.getPopularFilms(count);
    }
}
