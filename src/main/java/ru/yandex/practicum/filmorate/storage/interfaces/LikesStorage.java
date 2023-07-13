package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {
    List<Film> getCommonFilms(Integer userId, Integer friendId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopular(Integer count);

}