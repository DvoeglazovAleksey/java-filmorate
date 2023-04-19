package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long idFilm = 1;

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film add(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма оказалась раньше 28.12.1895.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28.12.1895.");
        } else {
            film.setId(idFilm++);
            films.put(film.getId(), film);
            log.info("Добавлен фильм {}", film.getName());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма оказалась ранее 28.12.1895.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28.12.1895.");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Нельзя обновить: фильм с id {} нет в базе данных", film.getId());
            throw new NullPointerException("Фильма нет в базе данных.");
        } else {
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}", film.getName());
        }
        return film;
    }
}
