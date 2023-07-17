package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

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
    private Set<Director> directors;
    private RatingMpa mpa;

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        directors.add(director);
    }

    public void deleteAllGenres() {
        genres.clear();
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }

}