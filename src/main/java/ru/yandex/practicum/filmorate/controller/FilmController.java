package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Optional<Film> findFilm(@PathVariable String id) {
        return filmService.findFilm(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> popular(@RequestParam(required = false) String count) {
        return filmService.popular(count);
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.addLikes(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.deleteLikes(id, userId);
    }
}