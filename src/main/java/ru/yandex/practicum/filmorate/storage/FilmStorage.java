package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    int deleteFilm(long id);

    List<Film> findAllFilms();

    Film findFilmById(long filmId);

    List<Film> getPopularFilms(int count);

    void addLikeToFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);
}
