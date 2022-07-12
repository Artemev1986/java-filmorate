package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public interface MPAStorage {
    List<MPA> getAllMPA();
    Optional<MPA> getMPAById(Integer id);
}
