package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void deleteFilmById(long id) {
        films.remove(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    @Override
    public List<Film> getPopularFilms(long count) {
        return null;
    }
}
