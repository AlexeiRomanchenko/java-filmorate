package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Positive
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private final Map<Integer, Boolean> grades = new HashMap<>();

    public int getUseful() {
        int useful = 0;
        for (var positive : grades.values()) {
            if (positive) {
                useful++;
            } else {
                useful--;
            }
        }
        return useful;
    }

    public void addGrade(Integer userId, boolean positive) {
        grades.put(userId, positive);
    }

    public void delGrade(Integer userId) {
        grades.remove(userId);
    }
}
