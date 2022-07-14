package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
public class LikeController {
    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
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
}
