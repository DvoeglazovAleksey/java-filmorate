package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre findGenreById(int genreId);

    Map<Long, Set<Genre>> getGenresByFilmList(List<Long> ids);

    List<Genre> getGenreByFilmId(long filmId);

    void addGenreToFilm(long filmId, int genreId);

    void deleteAllGenresFromFilm(long filmId);
}
