package ru.yandex.practicum.filorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa")
    public List<AgeRatingSystem> findAllMpa() {
        return mpaService.findAllMpa();
    }

    @GetMapping("/mpa/{Id}")
    public AgeRatingSystem findMpaById(@PathVariable("Id") Integer id) {
        return mpaService.findMpaById(id);
    }

}
