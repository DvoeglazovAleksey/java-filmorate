package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film findFilm(String id) {
        if (!(filmStorage.getFilms().containsKey(Long.parseLong(id)))) {
            throw new NullPointerException("Переданного фильма нет в базе с id" + id);
        }
        return filmStorage.getFilms().get(Long.parseLong(id));
    }

    public Film putLikes(String id, String userId) {
        Long idFilm = Long.parseLong(id);
        Long idUser = Long.parseLong(userId);
        if (!(filmStorage.getFilms().containsKey(idFilm)) || (!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException("Переданного фильма или пользователя нет в базе с id или userId" + id + "," + userId);
        } else {
            filmStorage.getFilms().get(idFilm).getLikes().add(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public Film deleteLikes(String id, String userId) {
        Long idFilm = Long.parseLong(id);
        Long idUser = Long.parseLong(userId);
        if (!(filmStorage.getFilms().containsKey(idFilm)) || (!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException("Переданного фильма или пользователя нет в базе с id или userId " + id + "," + userId);
        } else {
            filmStorage.getFilms().get(idFilm).getLikes().remove(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public List<Film> popular(String count) {
        if (count == null) {
            return filmStorage.getFilms().values().stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                    .limit(10)
                    .collect(Collectors.toList());
        } else if (Integer.parseInt(count) <= 0) {
            throw new ValidationException("count" + count);
        } else {
            return filmStorage.getFilms().values().stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                    .limit(Integer.parseInt(count))
                    .collect(Collectors.toList());
        }
    }
}
