package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class Film {
    @Positive
    private Integer id;
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Genre> genres;
    private RatingMpa mpa;

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteAllGenres() {
        genres.clear();
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }

}