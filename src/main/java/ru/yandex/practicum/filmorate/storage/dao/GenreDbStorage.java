package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
    public Map<Long, Set<Genre>> getGenresByFilmList(List<Long> ids) {
        String sql = "SELECT gf.film_id, gf.genre_id, g.name " +
                "FROM GenreFilm AS gf JOIN Genre as g on gf.genre_id = g.genre_id " +
                "WHERE gf.film_id IN (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Set<Genre>> genreByFilmList = new HashMap<>();

        namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = makeGenre(rs);
            Set<Genre> genres = genreByFilmList.getOrDefault(filmId, new HashSet<>());
            genres.add(genre);
            genreByFilmList.put(filmId, genres);
        });
        return genreByFilmList;
    }

    @Override
    public List<Genre> getGenreByFilmId(long filmId) {
        String sql = "SELECT g.* FROM GenreFilm AS gf JOIN Genre AS g ON gf.genre_id = g.genre_id WHERE " +
                "gf.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO GenreFilm (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void deleteAllGenresFromFilm(long filmId) {
        String sql = "DELETE FROM GenreFilm WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
