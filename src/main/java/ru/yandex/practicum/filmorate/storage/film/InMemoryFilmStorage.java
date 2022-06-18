package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private long id;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        id++;
        film.setId(id);
        films.put(id, film);
        log.debug("Adding new film with id: {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film with id (" + id + ")  not found");
        }
        films.put(film.getId(), film);
        log.debug("Film with id ({}) was updated", film.getId());
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Current film counts: {}", films.size());
        return List.copyOf(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id (" + id + ")  not found");
        }
        log.debug("Film search by id: {}", id);
        return films.get(id);
    }

    @Override
    public void deleteFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id (" + id + ")  not found");
        }
        films.remove(id);
        log.debug("Film with id ({}) was deleted", id);
    }
}
