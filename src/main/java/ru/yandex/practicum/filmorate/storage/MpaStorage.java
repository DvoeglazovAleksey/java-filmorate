package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;

public interface MpaStorage {
    List<Mpa> findAllMpa();

    Mpa findMpaById(int mpaId);

    Map<Long, Mpa> getMpaByFilmList(List<Long> ids);
}
