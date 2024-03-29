package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;


    @Autowired
    public FilmController(FilmService filmService, LikeService likeService) {
        this.filmService = filmService;
        this.likeService = likeService;

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
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
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

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") long count,
                                      @RequestParam(name = "year", required = false) Long year,
                                      @RequestParam(name = "genreId", required = false) Long genreId) {
        return filmService.getPopularFilms(count, year, genreId);
    }

    @PutMapping("/{id}/like/{userId}")
    public long addLike(@Valid @PathVariable long id, @PathVariable long userId) {
        likeService.addLike(id, userId);
        return userId;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public long deleteLike(@Valid @PathVariable long id, @PathVariable long userId) {
        likeService.deleteLike(id, userId);
        return userId;
    }

    @GetMapping("/director/{directorId}")
    @ResponseBody
    public List<Film> getDirectorFilmsSort(@Positive @PathVariable Long directorId, @RequestParam(name = "sortBy") String sortBy) {
        if (sortBy.equals("year")) {
            return filmService.getDirectorFilmsSortYear(directorId);
        } else if (sortBy.equals("likes")) {
            return filmService.getDirectorFilmsSortLikes(directorId);
        } else return null;

    }

    @GetMapping("search")
    public List<Film> searchForFilms(@RequestParam(required = false) String query,
                                     @RequestParam(required = false) String by) {
        return filmService.searchForFilms(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

}