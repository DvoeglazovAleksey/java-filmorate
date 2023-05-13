package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static java.lang.String.format;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final GenreService genreService;

    private final MpaService mpaService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreService genreService, MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    public Collection<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        updateGenreAndMpa(films);
        log.info("Получено фильмов: {}", films.size());
        return films;
    }

    public Film findFilmById(long filmId) {
        Film film;
        try {
            film = filmStorage.findFilmById(filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильма с id={} нет в базе", filmId);
            throw new NullPointerException(format("Фильма с id= %s нет в базе", filmId));
        }
        updateGenresByFilm(film);
        if (film.getMpa() != null) {
            film.setMpa(mpaService.findMpaById(film.getMpa().getId()));
        }
        log.info("Получили фильм по id={}", filmId);
        return film;
    }

    public Film addFilm(Film film) {
        validationBeforeAddFilm(film);
        Film createdFilm = filmStorage.addFilm(film);
        if (createdFilm.getGenres() != null) {
            film.getGenres().forEach(genre -> genreService.addGenreToFilm(film.getId(), genre.getId()));
        }
        log.info("Добавили фильм: {}", createdFilm.getName());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        validateFilmById(film.getId());
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            genreService.deleteAllGenresFromFilm(film.getId());
            Set<Genre> genres = film.getGenres();
            genres.forEach(genre -> genreService.addGenreToFilm(film.getId(), genre.getId()));
            genres.clear();
            updateGenresByFilm(film);
        }
        if (film.getMpa() != null) {
            film.setMpa(mpaService.findMpaById(film.getMpa().getId()));
        }
        log.info("Обновлен фильм c id = {}", film.getId());
        return film;
    }

    public void addLikes(long filmId, long userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikes(long filmId, long userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        log.info("Пользователь id = {} удалил лайк у фильма id = {}", userId, filmId);
        filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            log.error("popular: Параметр количества фильмов меньше или равен нулю.");
            throw new ValidationException("Параметр количества фильмов меньше или равен нулю: count = " + count);
        }
        List<Film> films = filmStorage.getPopularFilms(count);
        updateGenreAndMpa(films);
        return films;
    }

    private void updateGenresByFilm(Film film) {
        List<Long> filmIds = new ArrayList<>();
        filmIds.add(film.getId());
        Map<Long, Set<Genre>> genres2 = genreService.getGenresByFilmList(filmIds);
        if (genres2.get(film.getId()) != null) {
            film.setGenres(genres2.get(film.getId()));
        }
    }

    private void updateGenreAndMpa(List<Film> films) {
        List<Long> filmId = new ArrayList<>();
        for (Film film : films) {
            filmId.add(film.getId());
        }
        Map<Long, Set<Genre>> genres = genreService.getGenresByFilmList(filmId);
        Map<Long, Mpa> mpa = mpaService.getMpaByFilmList(filmId);
        filmId.clear();
        if (!(films.isEmpty())) {
            for (Film film : films) {
                if (genres.get(film.getId()) != null) {
                    film.setGenres(genres.get(film.getId()));
                }
                if (mpa.get(film.getId()) != null) {
                    film.setMpa(mpa.get(film.getId()));
                }
            }
        }
    }

    private void validateFilmById(long filmId) {
        try {
            if (filmStorage.findFilmById(filmId) == null) {
                log.error("Фильма с id={} нет в базе", filmId);
                throw new NullPointerException(format("Фильса с id= %s нет в базе", filmId));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Фильса с id= %s нет в базе", filmId));
        }
    }

    private void validateUserById(long userId) {
        try {
            if (userStorage.findUserById(userId) == null) {
                log.error("Пользователя с id={} нет в базе", userId);
                throw new NullPointerException(format("Пользователя с id= %s нет в базе", userId));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Пользователя с id= %s нет в базе", userId));
        }
    }

    private void validationBeforeAddFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть раньше {}", LocalDate.of(1895, 12, 28));
            throw new ValidationException(format("Дата релиза не может быть раньше %tD", LocalDate.of(1895, 12, 28)));
        }
        List<Film> films = filmStorage.findAllFilms();
        for (Film film1 : films) {
            if (film.getName().equals(film1.getName()) && film.getReleaseDate().equals(film1.getReleaseDate())
                    && film.getDuration() == film1.getDuration()) {
                log.error("Фильм с name={}, releaseDate={}, duration={}, уже существует", film.getName(),
                        film.getReleaseDate(), film.getDuration());
                throw new ValidationException("Фильм с name=" + film.getName() + ", releaseDate=" +
                        film.getReleaseDate() + ", duration= " + film.getDuration() + ", уже существует");
            }
        }
    }
}
