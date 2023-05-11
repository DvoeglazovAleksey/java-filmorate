package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    int deleteFilm(long id);

    List<Film> findAllFilms();

    Film findFilmById(long filmId);

    List<Genre> findAllGenres();

    List<Film> popularFilms(int count);

    List<Mpa> findAllMpa();

    Mpa findMpaById(int mpaId);

    void addLikeToFilm(long filmId, long userId);

    Genre findGenreById(int genreId);

    void deleteLikeFromFilm(long filmId, long userId);
}
