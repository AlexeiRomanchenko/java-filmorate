package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingMpa {

    @Positive
    protected int id;

    @NotBlank
    protected String name;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RatingMpa{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

}