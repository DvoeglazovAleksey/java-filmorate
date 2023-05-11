package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO Film (name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
        log.info("Фильм {} сохранен", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE Film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = ?";
        int amountOperations = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (amountOperations > 0) {
            deleteAllGenresFromFilm(film.getId());
            Set<Genre> genres = film.getGenres();
            genres.forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
            genres.clear();
            genres.addAll(getGenreByFilmId(film.getId()));
            return film;
        }
        log.debug("Фильм с id={} не найден", film.getId());
        throw new ValidationException(String.format("Фильм с id = %s не найден", film.getId()));
    }

    @Override
    public int deleteFilm(long id) {
        return jdbcTemplate.update("DELETE FROM Film WHERE id = ?", id);
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN Mpa AS m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film film : films) {
            film.getGenres().addAll(getGenreByFilmId(film.getId()));
            film.getLikes().addAll(getFilmLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film findFilmById(long filmId) {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), filmId);
        if (film != null) {
            film.getGenres().addAll(getGenreByFilmId(filmId));
            film.getLikes().addAll(getFilmLikes(filmId));
        }
        return film;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "SELECT * FROM Genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre findGenreById(int genreId) {
        String sql = "SELECT * FROM Genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    @Override
    public void addLikeToFilm(long filmId, long userId) {
        try {
            String sql = "INSERT INTO Likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataAccessException exception) {
            log.error("Пользователь id = {} уже поставил лайк фильму id = {}", userId, filmId);
            throw new ValidationException(format("Пользователь id = %s уже поставил лайк фильму id = %s",
                    userId, filmId));
        }
    }

    @Override
    public void deleteLikeFromFilm(long filmId, long userId) {
        String sql = "DELETE FROM Likes WHERE (film_id = ? AND user_id = ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> popularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM Film AS f JOIN Mpa AS m ON f.mpa_id = m.mpa_id" +
                " LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count FROM Likes GROUP BY film_id ORDER BY " +
                "likes_count) AS popular on f.film_id = popular.film_id ORDER BY popular.likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public List<Mpa> findAllMpa() {
        String sql = "SELECT * FROM Mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa findMpaById(int mpaId) {
        String sql = "SELECT * FROM Mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), mpaId);
    }

    private List<Long> getFilmLikes(long filmId) {
        String sql = "SELECT user_id FROM Likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNun) -> rs.getLong("user_id"), filmId);
    }

    private List<Genre> getGenreByFilmId(long filmId) {
        String sql = "SELECT g.* FROM GenreFilm AS gf JOIN Genre AS g ON gf.genre_id = g.genre_id WHERE " +
                "gf.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO GenreFilm (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private void deleteAllGenresFromFilm(long filmId) {
        String sql = "DELETE FROM GenreFilm WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder().id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa.name")).build();
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id")).name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa.name")).build()).build();
    }
}
