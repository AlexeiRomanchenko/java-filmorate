package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.description.LogMPA;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@Validated
@RequiredArgsConstructor
public class MPAController {
    private final MpaService mpaService;

    @GetMapping
    public List<RatingMpa> getRatingsMpa() {
        log.info(LogMPA.GET_ALL_MPA_REQUEST.getMessage());
        return mpaService.getRatingsMpa();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingMpaById(@Positive @PathVariable Integer id) {
        log.info(LogMPA.GET_MPA_REQUEST.getMessage(), id);
        return mpaService.getRatingMpaById(id);
    }

}