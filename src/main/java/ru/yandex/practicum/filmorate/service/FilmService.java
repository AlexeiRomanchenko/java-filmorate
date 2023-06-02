package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.description.LogMessagesFilms;
import ru.yandex.practicum.filmorate.description.LogMessagesUsers;
import ru.yandex.practicum.filmorate.exception.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    Map<Integer, Integer> likes = new HashMap<>();
    ArrayList<Film> popularFilms = new ArrayList<>();
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film findFilm(int id) {
        if (!filmStorage.getIdsAllFilms().contains(id)) {
            throw new ObjectNotFoundException(LogMessagesFilms.FILM_NO_FOUND_WITH_ID.getMessage());
        }
        return filmStorage.getById(id);
    }

    public void addLike(int id, int userId) {

        if (findFilm(id).getLikes()!= null && findFilm(id).getLikes().contains((long) userId)) {
            throw new ActionHasAlreadyDoneException(LogMessagesFilms.USER_ALREDY_ADD_LIKE.getMessage());
        } else {
            findFilm(id).setLikes(userId);
        }
    }

    public void deleteLike(int id, int userId) {

        if (userId < 0) {
            throw new ObjectNotFoundException(LogMessagesUsers.ID_NOT_POSITIVE.getMessage());
        }

        findFilm(id).deleteLike(userId);

    }

    public ArrayList<Film> getPopularFilms(int count) {
        popularFilms.clear();

        for (Film film : filmStorage.getFilms()) {

            if(film.getLikes() != null) {
                likes.put(film.getId(), film.getLikes().size());
            }
            else {
                int NO_LIKES = 0;
                likes.put(film.getId(), NO_LIKES);
            }

        }

        likes = likes.entrySet()
                .stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        for (Integer id : likes.keySet()) {

            if (popularFilms.isEmpty() || !popularFilms.contains(findFilm(id))) {
                popularFilms.add(findFilm(id));
            }

        }
        return popularFilms;
    }

}