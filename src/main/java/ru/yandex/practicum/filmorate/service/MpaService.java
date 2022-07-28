package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> ListOfMpa = mpaStorage.getAllMpa();
        log.debug("Get all MPA. Current MPA counts: {}", ListOfMpa.size());
        return ListOfMpa;
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = mpaStorage.getMpaById(id).
                orElseThrow(() -> new NotFoundException("MPA with id (" + id + ") not found")
                );
        log.debug("Get MPA by id: {}", id);
        return mpa;
    }
}
