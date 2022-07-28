package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public LikeService(LikeStorage likeStorage, FilmStorage filmStorage, UserStorage userStorage, FeedStorage feedStorage) {
        this.likeStorage = likeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public void addLike(long filmId, long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id (" + userId + ") not found"));
        Film film= filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id (" + filmId + ") not found"));
        likeStorage.addLike(filmId, userId);
        feedStorage.addInFeed(userId, "LIKE", "ADD", filmId);
        log.debug("Like for the {} added by {}",
                film.getName(),
                user.getName());
    }

    public void deleteLike(long filmId, long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id (" + userId + ") not found"));
        Film film= filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id (" + filmId + ") not found"));
        likeStorage.deleteLike(filmId, userId);
        feedStorage.addInFeed(userId, "LIKE", "REMOVE", filmId);
        log.debug("Like for the {} deleted by {}",
                film.getName(),
                user.getName());
    }
}
