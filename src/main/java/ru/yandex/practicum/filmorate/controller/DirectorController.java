package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;


import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping()
    public List<Director> getDirector() {
        return directorService.getAllDirector();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@Valid @PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping()
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping()
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public long deleteDirectorById(@Valid @PathVariable long id) {
        directorService.deleteDirectorById(id);
        return id;
    }
}
