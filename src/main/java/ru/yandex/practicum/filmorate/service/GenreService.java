package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenre() {
        List<Genre> genres = genreStorage.getAllGenre();
        log.debug("Get all genres. Current genre counts: {}", genres.size());
        return genres;
    }

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.getGenreById(id).
                orElseThrow(() -> new NotFoundException("Genre with id (" + id + ") not found")
                );
        log.debug("Get genre by id: {}", id);
        return genre;
    }
}
