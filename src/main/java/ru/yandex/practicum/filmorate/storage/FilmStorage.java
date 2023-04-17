package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface FilmStorage {
    Film create(Film film);

    Film put(Film film);

    Map<Long, Film> getFilms();
}
