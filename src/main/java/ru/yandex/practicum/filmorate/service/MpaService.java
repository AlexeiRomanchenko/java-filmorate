package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMPA;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.db.RatingMpaDbStorage;

import java.util.List;

@Service
public class MpaService {
    private final RatingMpaDbStorage ratingMpaDbStorage;

    public MpaService(RatingMpaDbStorage ratingMpaDbStorage) {
        this.ratingMpaDbStorage = ratingMpaDbStorage;
    }

    public RatingMpa getRatingMpaById(int id) {
        RatingMpa mpa = ratingMpaDbStorage.getRatingMpaById(id);
        if (mpa == null) {
            throw new ObjectNotFoundException(LogMPA.NO_FOUND_MPA.getMessage());
        }
        return mpa;
    }

    public List<RatingMpa> getRatingsMpa() {
        return ratingMpaDbStorage.getRatingsMpa();
    }

}