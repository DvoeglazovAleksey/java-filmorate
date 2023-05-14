package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaDbStorage) {
        this.mpaStorage = mpaDbStorage;
    }

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(int mpaId) {
        try {
            return mpaStorage.findMpaById(mpaId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Mpa с id={} не найден", mpaId);
            throw new NullPointerException(format("Mpa с id = %s не найден", mpaId));
        }
    }

    protected Map<Long, Mpa> getMpaByFilmList(List<Long> ids) {
        return mpaStorage.getMpaByFilmList(ids);
    }
}
