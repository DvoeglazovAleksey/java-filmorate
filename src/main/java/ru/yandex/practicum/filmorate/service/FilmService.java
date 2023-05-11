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
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAllFilms() {
        log.info("Сохранено фильмов: {}", filmStorage.findAllFilms().size());
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(long filmId) {
        Film film;
        try {
            film = filmStorage.findFilmById(filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильма с id={} нет в базе", filmId);
            throw new NullPointerException(format("Фильма с id= %s нет в базе", filmId));
        }
        log.info("Получили фильм по id={}", filmId);
        return film;
    }

    public Film addFilm(Film film) {
        validationBeforeAddFilm(film);
        Film createdFilm = filmStorage.addFilm(film);
        log.info("Добавили фильм: {}", createdFilm.getName());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        validateFilmById(film.getId());
        log.info("Обновлен фильм c id = {}", film.getId());
        return filmStorage.updateFilm(film);
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

    public List<Film> popular(int count) {
        if (count <= 0) {
            log.error("popular: Параметр количества фильмов меньше или равен нулю.");
            throw new ValidationException("Параметр количества фильмов меньше или равен нулю: count = " + count);
        }
        return filmStorage.popularFilms(count);
    }

    public List<Genre> findAllGenres() {
        log.info("Получен список жанров");
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(int genreId) {
        try {
            return filmStorage.findGenreById(genreId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с id = {} не найден", genreId);
            throw new NullPointerException(format("Жанр с id = %s не найден", genreId));
        }
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public Mpa findMpaById(int mpaId) {
        try {
            return filmStorage.findMpaById(mpaId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Mpa с id={} не найден", mpaId);
            throw new NullPointerException(format("Mpa с id = %s не найден", mpaId));
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
