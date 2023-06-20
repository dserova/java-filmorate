package ru.yandex.practicum.filorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.model.Genre;
import ru.yandex.practicum.filorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("/genres/{Id}")
    public Genre findGenresById(@PathVariable("Id") Integer id) {
        return genreService.findGenresById(id);
    }

}
