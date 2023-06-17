package ru.yandex.practicum.filorate.storage;

import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.model.Genre;

import java.util.List;

public interface UtilityStorage {

    List<Genre> findAllGenres();

    Genre findGenresById(int id);

    List<AgeRatingSystem> findAllMpa();

    AgeRatingSystem findMpaById(int id);

}
