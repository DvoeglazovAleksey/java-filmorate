package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 1;
    private final LocalDate MINIMAL_DATE_RELEASE = LocalDate.of(1985,12,28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(MINIMAL_DATE_RELEASE)) {
            log.error("Дата релиза фильма оказалась ранее 28 12 1895 года.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28 12 1895 года.");
        } else {
            film.setId(idFilm++);
            films.put(film.getId(), film);
            log.info("Добавлен фильм {}", film.getName());
        }
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(MINIMAL_DATE_RELEASE)) {
            log.error("Дата релиза фильма оказалась ранее 28 12 1895 года.");
            throw new ValidationException("Дата релиза фильма не может быть ранее 28 12 1895 года.");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Нельзя обновить: фильм с id {} нет в базе данных", film.getId());
            throw new ValidationException("Фильма нет в базе данных.");
        } else {
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}", film.getName());
        }
        return film;
    }
}