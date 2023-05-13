package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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

    @Override
    public Map<Long, Mpa> getMpaByFilmList(List<Long> ids) {
        String sql = "SELECT f.*, m.name "
                + "FROM Film AS f LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id  IN (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Mpa> mpaByFilmList = new HashMap<>();

        namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            long filmId = rs.getLong("film_id");
            Mpa mpa = makeMpa(rs);
            mpaByFilmList.put(filmId, mpa);
        });
        return mpaByFilmList;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder().id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa.name")).build();
    }
}
