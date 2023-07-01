package ru.yandex.practicum.filorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.dao.db.DbGenreDao;
import ru.yandex.practicum.filorate.model.Genre;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {

    private final DbGenreDao genreDao;

    public List<Genre> findAllGenres() {
        return genreDao.findAllGenres();
    }

    public Genre findGenresById(int id) {
        return genreDao.findGenresById(id);
    }


}
