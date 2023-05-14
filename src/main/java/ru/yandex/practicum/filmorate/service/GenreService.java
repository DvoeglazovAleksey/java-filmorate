package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@Service
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAllGenres() {
        log.info("Получен список жанров");
        return genreStorage.findAllGenres();
    }

    protected void addGenreToFilm(long filmId, int genreId) {
        genreStorage.addGenreToFilm(filmId, genreId);
    }

    protected void deleteAllGenresFromFilm(long filmId) {
        genreStorage.deleteAllGenresFromFilm(filmId);
    }

    public Genre findGenreById(int genreId) {
        try {
            return genreStorage.findGenreById(genreId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с id = {} не найден", genreId);
            throw new NullPointerException(format("Жанр с id = %s не найден", genreId));
        }
    }

    protected Map<Long, Set<Genre>> getGenresByFilmList(List<Long> ids) {
        return genreStorage.getGenresByFilmList(ids);
    }

    protected List<Genre> getGenreByFilmId(long filmId) {
        return genreStorage.getGenreByFilmId(filmId);
    }
}
