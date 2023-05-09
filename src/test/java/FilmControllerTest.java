import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filorate.controller.FilmController;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.Film;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    @Test
    public void addFilm() throws ValidationException {

        String description = "";
        for (int i = 0; i < 300; i++) {
            description += "q";
        }

        FilmController filmController = new FilmController();

        assertEquals(true, filmController.validation(new Film(1, "name", "description", Date.from(Instant.now()), 20)), "44");
        assertEquals(false, filmController.validation(new Film(1, "", "description", new Date(2000, 1, 1), 20)), "название не может быть пустым");

        assertEquals(false, filmController.validation(new Film(1, "name", description, new Date(2000, 1, 1), 20)));

        assertEquals(false, filmController.validation(new Film(1, "name", "description", new Date(-10, 1, 1), 20)), "до 8 декабря 1895 года");
        assertEquals(true, filmController.validation(new Film(1, "name", "description", new Date(-5, 12, 28), 20)), "до 8 декабря 1895 года");
        assertEquals(true, filmController.validation(new Film(1, "name", "description", new Date(100, 12, 28), 20)), "до 8 декабря 1895 года");

        assertEquals(false, filmController.validation(new Film(1, "name", "description", new Date(2000, 1, 1), -20)), "продолжительность фильма должна быть положительной");
        assertEquals(true, filmController.validation(new Film(1, "name", "description", new Date(2000, 1, 1), 0)), "продолжительность фильма должна быть положительной");

    }

}
