import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filorate.controller.FilmController;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.Film;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    @Test
    public void addFilm() throws ValidationException {

        FilmController filmController = new FilmController();
        Film film;
        ValidationException thrown;
        String description = "";

        for (int i = 0; i < 300; i++) {
            description += "q";
        }

        film = new Film(1, "name", "description", Date.from(Instant.now()), 20);
        assertEquals(filmController.create(film), new ResponseEntity<Film>(film, HttpStatus.OK));

        thrown = assertThrows(ValidationException.class, () -> {
            filmController.create(new Film(1, "", "description", new Date(2000, 1, 1), 20));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "название не может быть пустым");

        String finalDescription = description;
        thrown = assertThrows(ValidationException.class, () -> {
            filmController.create(new Film(1, "name", finalDescription, new Date(2000, 1, 1), 20));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "максимальная длина описания — 200 символов");

        thrown = assertThrows(ValidationException.class, () -> {
            filmController.create(new Film(1, "name", "description", new Date(-10, 1, 1), 20));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "дата релиза — не раньше 28 декабря 1895 года");

        film = new Film(1, "name", "description", new Date(-5, 12, 28), 20);
        assertEquals(filmController.create(film), new ResponseEntity<Film>(film, HttpStatus.OK));

        film = new Film(1, "name", "description", new Date(100, 12, 28), 20);
        assertEquals(filmController.create(film), new ResponseEntity<Film>(film, HttpStatus.OK));

        thrown = assertThrows(ValidationException.class, () -> {
            filmController.create(new Film(1, "name", "description", new Date(2000, 1, 1), -20));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "продолжительность фильма должна быть положительной");

        film = new Film(1, "name", "description", new Date(2000, 1, 1), 0);
        assertEquals(filmController.create(film), new ResponseEntity<Film>(film, HttpStatus.OK));

    }

}
