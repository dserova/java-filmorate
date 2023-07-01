package ru.yandex.practicum.filorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.dao.db.DbMpaDao;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final DbMpaDao mpaDao;

    public List<AgeRatingSystem> findAllMpa() {
        return mpaDao.findAllMpa();
    }

    public AgeRatingSystem findMpaById(int id) {
        return mpaDao.findMpaById(id);
    }

}
