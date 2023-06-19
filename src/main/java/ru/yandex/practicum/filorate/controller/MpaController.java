package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.storage.db.DbMpaStorage;

import java.util.List;

@RestController
@Slf4j
public class MpaController {
    private final DbMpaStorage dbMpaStorage;

    public MpaController(DbMpaStorage dbMpaStorage) {
        this.dbMpaStorage = dbMpaStorage;
    }

    @GetMapping("/mpa")
    public List<AgeRatingSystem> findAllMpa() {
        return dbMpaStorage.findAllMpa();
    }

    @GetMapping("/mpa/{Id}")
    public AgeRatingSystem findMpaById(@PathVariable("Id") Integer id) {
        return dbMpaStorage.findMpaById(id);
    }

}
