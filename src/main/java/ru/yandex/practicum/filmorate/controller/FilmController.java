package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmService.findAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film findFilm(@PathVariable String id) {
        return filmService.findFilm(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> popular(@RequestParam(required = false) String count) {
        return filmService.popular(count);
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film put(@Valid @RequestBody Film film) {
        return filmService.putFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film putLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.putLikes(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.deleteLikes(id, userId);
    }
}