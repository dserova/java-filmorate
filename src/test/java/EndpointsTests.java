import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filorate.FilmorateApplication;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.dao.db.DbUserDao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EndpointsTests {

    @LocalServerPort
    private Integer port;

    void template(String endpoint, String RequestBody, String ResponseBody, MediaType RequestType, MediaType ResponseType, HttpStatus StatusResponse, HttpMethod method) throws ValidationException {
        String bodyJsonRequest = RequestBody;
        // print to IO
        System.out.println("------------TEST ADDRESS--------------");
        System.out.println("http://localhost:" + port + endpoint);
        System.out.println("------------TEST ADDRESS--------------");
        System.out.println("------------  TEST BODY --------------");
        System.out.println(bodyJsonRequest);
        System.out.println("------------  TEST BODY --------------");
        // https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
        // Test with WebTest
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        client.method(method).uri(endpoint)
                .header("Content-Type", RequestType.toString())
                .bodyValue(bodyJsonRequest)
                .exchange()
                .expectAll(
                        // check status code
                        spec -> spec.expectStatus().isEqualTo(StatusResponse),
                        // check content-type
                        spec -> spec.expectHeader().contentType(ResponseType),
                        // check body
                        spec -> spec.expectBody().json(ResponseBody)
                );
        // Notes:
        //
        // check body
        // 0 ret type
        // .returnResult(User.class);
        // 1 type result
        // .expectBody(String.class).returnResult();
        // .expectBody().isEmpty(); // null content check
        // .expectBody(Void.class); // null content check
        // 2 json result check
        // .expectBody().json("{\"id\":1,\"email\":\"tester@test.test\",\"login\":\"tester\",\"name\":\"tester\",\"birthday\":\"1950-02-01\",\"friends\":[],\"filmsLikes\":[]}");
        // .expectBody().json(bodyJsonRequest);
        // .jsonPath("$[0].name").isEqualTo("tester")
        // 3 is list
        // .expectBodyList(Name.class).hasSize(3).contains(someone);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static Film testMakeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date"),
                rs.getInt("duration"),
                rs.getInt("rate"),
                new ArrayList<>(),
                new AgeRatingSystem(0, "")
        );
    }

    void checkDb() {
        String sqlQueryAllFilms = "SELECT * FROM public.FILM";
        String sqlQueryCountFilms = "SELECT COUNT(*) FROM public.FILM";
        String sqlQueryAllUsers = "SELECT * FROM public.USER";
        String sqlQueryCountUsers = "SELECT COUNT(*) FROM public.USER";

        List<Film> films = jdbcTemplate.query(sqlQueryAllFilms, EndpointsTests::testMakeFilm);
        System.out.println("------------FILMS in DB--------------");
        StringBuilder testFilms = new StringBuilder();
        for (Film film : films) {
            testFilms.append(film.toString());
        }
        System.out.println(testFilms);
        System.out.println("------------FILMS in DB--------------");

        Integer film = jdbcTemplate.queryForObject(sqlQueryCountFilms, Integer.class);
        System.out.println("------------FILMS COUNT in DB--------------");
        System.out.println(film.toString());

        Assertions.assertThat(film.toString())
                .hasToString("2");
        Assertions.assertThat(testFilms)
                .hasToString("Film(id=1, name=Film Updated, description=New film update decription, releaseDate=1989-04-17, duration=190, rate=4, genres=[], mpa=AgeRatingSystem(id=0, name=))Film(id=2, name=New film, description=New film about friends, releaseDate=1999-04-30, duration=120, rate=0, genres=[], mpa=AgeRatingSystem(id=0, name=))");

        List<User> users = jdbcTemplate.query(sqlQueryAllUsers, DbUserDao::makeUser);
        System.out.println("------------USERS in DB--------------");
        StringBuilder testUsers = new StringBuilder();
        for (User user : users) {
            testUsers.append(user.toString());
        }
        System.out.println(testUsers);
        System.out.println("------------USERS in DB--------------");

        Integer user = jdbcTemplate.queryForObject(sqlQueryCountUsers, Integer.class);
        System.out.println("------------USERS COUNT in DB--------------");
        System.out.println(user.toString());

        Assertions.assertThat(user.toString())
                .hasToString("3");
        Assertions.assertThat(testUsers)
                .hasToString("User(id=1, email=mail@yandex.ru, login=doloreUpdate, name=est adipisicing, birthday=1976-09-20, friends=[], filmsLikes=[])User(id=2, email=friend@mail.ru, login=friend, name=friend adipisicing, birthday=1976-08-20, friends=[], filmsLikes=[])User(id=3, email=friend@common.ru, login=common, name=common, birthday=2000-08-20, friends=[], filmsLikes=[])");

    }

    @Test
    void caseTests() throws IOException {
        //----USERS-----
        // User create
        this.template("/users",
                "{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"email\": \"mail@mail.ru\",\n" +
                        "\t\"login\": \"dolore\",\n" +
                        "\t\"name\": \"Nick Name\",\n" +
                        "\t\"birthday\": \"1946-08-20\",\n" +
                        "\t\"friends\": null,\n" +
                        "\t\"filmsLikes\": null\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // User create Fail login
        this.template("/users",
                "{\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"email\": \"yandex@mail.ru\",\n" +
                        "  \"birthday\": \"2446-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User create Fail email
        this.template("/users",
                "{\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User create Fail birthday
        this.template("/users",
                "{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"test@mail.ru\",\n" +
                        "  \"birthday\": \"2446-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User update
        this.template("/users",
                "{\n" +
                        "  \"login\": \"doloreUpdate\",\n" +
                        "  \"name\": \"est adipisicing\",\n" +
                        "  \"id\": 1,\n" +
                        "  \"email\": \"mail@yandex.ru\",\n" +
                        "  \"birthday\": \"1976-09-20\"\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"email\": \"mail@yandex.ru\",\n" +
                        "\t\"login\": \"doloreUpdate\",\n" +
                        "\t\"name\": \"est adipisicing\",\n" +
                        "\t\"birthday\": \"1976-09-20\",\n" +
                        "\t\"friends\": null,\n" +
                        "\t\"filmsLikes\": null\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // User update unknown
        this.template("/users",
                "{\n" +
                        "  \"login\": \"doloreUpdate\",\n" +
                        "  \"name\": \"est adipisicing\",\n" +
                        "  \"id\": 9999,\n" +
                        "  \"email\": \"mail@yandex.ru\",\n" +
                        "  \"birthday\": \"1976-09-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );

        // User get All
        this.template("/users",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"email\": \"mail@yandex.ru\",\n" +
                        "\t\t\"login\": \"doloreUpdate\",\n" +
                        "\t\t\"name\": \"est adipisicing\",\n" +
                        "\t\t\"birthday\": \"1976-09-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FRIENDS1-----
        // Friend Create
        this.template("/users",
                "{\n" +
                        "  \"login\": \"friend\",\n" +
                        "  \"name\": \"friend adipisicing\",\n" +
                        "  \"email\": \"friend@mail.ru\",\n" +
                        "  \"birthday\": \"1976-08-20\"\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"email\": \"friend@mail.ru\",\n" +
                        "\t\"login\": \"friend\",\n" +
                        "\t\"name\": \"friend adipisicing\",\n" +
                        "\t\"birthday\": \"1976-08-20\",\n" +
                        "\t\"friends\": null,\n" +
                        "\t\"filmsLikes\": null\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Common Friend Create
        this.template("/users",
                "{\n" +
                        "  \"login\": \"common\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"friend@common.ru\",\n" +
                        "  \"birthday\": \"2000-08-20\"\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 3,\n" +
                        "\t\"email\": \"friend@common.ru\",\n" +
                        "\t\"login\": \"common\",\n" +
                        "\t\"name\": \"common\",\n" +
                        "\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\"friends\": null,\n" +
                        "\t\"filmsLikes\": null\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // User get by id=1
        this.template("/users/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"email\": \"mail@yandex.ru\",\n" +
                        "\t\"login\": \"doloreUpdate\",\n" +
                        "\t\"name\": \"est adipisicing\",\n" +
                        "\t\"birthday\": \"1976-09-20\",\n" +
                        "\t\"friends\": [],\n" +
                        "\t\"filmsLikes\": []\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User get unknown with id=9999
        this.template("/users/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );

        // Friend get user id=2
        this.template("/users/2",
                "",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"email\": \"friend@mail.ru\",\n" +
                        "\t\"login\": \"friend\",\n" +
                        "\t\"name\": \"friend adipisicing\",\n" +
                        "\t\"birthday\": \"1976-08-20\",\n" +
                        "\t\"friends\": [],\n" +
                        "\t\"filmsLikes\": []\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FRIENDS2-----
        // User get friends common empty
        this.template("users/1/friends/common/2",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=1 add friend id=2
        this.template("users/1/friends/2",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // User id=1 add friend unknown id=-1
        this.template("users/1/friends/-1",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );

        // User id=1 get friends
        this.template("users/1/friends",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"email\": \"friend@mail.ru\",\n" +
                        "\t\t\"login\": \"friend\",\n" +
                        "\t\t\"name\": \"friend adipisicing\",\n" +
                        "\t\t\"birthday\": \"1976-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=2 get friends. Not confirm
        this.template("users/2/friends",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Empty Common friends to user id=1 with user id=2
        this.template("users/1/friends/common/2",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=1 add  friend id=3
        this.template("users/1/friends/3",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // User id=1 get 2 friends
        this.template("users/1/friends",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"email\": \"friend@mail.ru\",\n" +
                        "\t\t\"login\": \"friend\",\n" +
                        "\t\t\"name\": \"friend adipisicing\",\n" +
                        "\t\t\"birthday\": \"1976-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=2 add  friend id=3
        this.template("users/2/friends/3",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // User id=2 get 1 friends
        this.template("users/2/friends",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Common friend to user id=1 with user id=2
        this.template("users/1/friends/common/2",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=1 remove friend id=2
        this.template("users/1/friends/2",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.DELETE
        );

        // User id=1 get common with user id=2
        this.template("users/1/friends/common/2",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=2 get common with user id=1
        this.template("users/2/friends/common/1",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // User id=1 get 1 friend
        this.template("users/1/friends",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"email\": \"friend@common.ru\",\n" +
                        "\t\t\"login\": \"common\",\n" +
                        "\t\t\"name\": \"common\",\n" +
                        "\t\t\"birthday\": \"2000-08-20\",\n" +
                        "\t\t\"friends\": [],\n" +
                        "\t\t\"filmsLikes\": []\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FILMS-----
        // Film get All
        this.template("/films",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film id=1 create
        this.template("/films",
                "{\n" +
                        "  \"name\": \"nisi eiusmod\",\n" +
                        "  \"description\": \"adipisicing\",\n" +
                        "  \"releaseDate\": \"1967-03-25\",\n" +
                        "  \"duration\": 100,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"nisi eiusmod\",\n" +
                        "\t\"description\": \"adipisicing\",\n" +
                        "\t\"releaseDate\": \"1967-03-25\",\n" +
                        "\t\"duration\": 100,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": [],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Film create Fail name
        this.template("/films",
                "{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"description\": \"Description\",\n" +
                        "  \"releaseDate\": \"1900-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 0,\n" +
                        "\t\"name\": \"\",\n" +
                        "\t\"description\": \"Description\",\n" +
                        "\t\"releaseDate\": \"1900-03-25\",\n" +
                        "\t\"duration\": 200,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": null,\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail description
        this.template("/films",
                "{\n" +
                        "  \"name\": \"Film name\",\n" +
                        "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                        "    \"releaseDate\": \"1900-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 0,\n" +
                        "\t\"name\": \"Film name\",\n" +
                        "\t\"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                        "\t\"releaseDate\": \"1900-03-25\",\n" +
                        "\t\"duration\": 200,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": null,\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail releaseDate
        this.template("/films",
                "{\n" +
                        "  \"name\": \"Name\",\n" +
                        "  \"description\": \"Description\",\n" +
                        "  \"releaseDate\": \"1890-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 0,\n" +
                        "\t\"name\": \"Name\",\n" +
                        "\t\"description\": \"Description\",\n" +
                        "\t\"releaseDate\": \"1890-03-25\",\n" +
                        "\t\"duration\": 200,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": null,\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail duration
        this.template("/films",
                "{\n" +
                        "  \"name\": \"Name\",\n" +
                        "  \"description\": \"Descrition\",\n" +
                        "  \"releaseDate\": \"1980-03-25\",\n" +
                        "  \"duration\": -200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 0,\n" +
                        "\t\"name\": \"Name\",\n" +
                        "\t\"description\": \"Descrition\",\n" +
                        "\t\"releaseDate\": \"1980-03-25\",\n" +
                        "\t\"duration\": -200,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": null,\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film update
        this.template("/films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 2}\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // Film update unknown
        this.template("/films",
                "{\n" +
                        "  \"id\": 9999,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );

        // Film get All
        this.template("/films",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"Film Updated\",\n" +
                        "\t\t\"description\": \"New film update decription\",\n" +
                        "\t\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\t\"duration\": 190,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"PG\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film get Popular
        this.template("/films/popular",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"Film Updated\",\n" +
                        "\t\t\"description\": \"New film update decription\",\n" +
                        "\t\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\t\"duration\": 190,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"PG\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        //----FILM-----
        // Film id=2 create
        this.template("/films",
                "{\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}]\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"name\": \"New film\",\n" +
                        "\t\"description\": \"New film about friends\",\n" +
                        "\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\"duration\": 120,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 1,\n" +
                        "\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Film id=1 get
        this.template("/films/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"PG\"\n" +
                        "\t}\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film id=9999 get not found
        this.template("/films/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );

        //----Like-----
        // Film id=2 add Like from user id=1
        this.template("/films/2/like/1",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // Film most popular film
        this.template("/films/popular?count=1",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"New film\",\n" +
                        "\t\t\"description\": \"New film about friends\",\n" +
                        "\t\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\t\"duration\": 120,\n" +
                        "\t\t\"rate\": 5,\n" +
                        "\t\t\"genres\": [\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\": 1,\n" +
                        "\t\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t\t}\n" +
                        "\t\t],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 3,\n" +
                        "\t\t\t\"name\": \"PG-13\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film id=2 add Like from user id=1
        this.template("/films/2/like/1",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.DELETE
        );

        // Film get all popular film
        this.template("/films/popular",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"New film\",\n" +
                        "\t\t\"description\": \"New film about friends\",\n" +
                        "\t\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\t\"duration\": 120,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\": 1,\n" +
                        "\t\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t\t}\n" +
                        "\t\t],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 3,\n" +
                        "\t\t\t\"name\": \"PG-13\"\n" +
                        "\t\t}\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"Film Updated\",\n" +
                        "\t\t\"description\": \"New film update decription\",\n" +
                        "\t\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\t\"duration\": 190,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"PG\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film id=2 remove Like from user id=-2  not found
        this.template("/films/2/like/-2",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.DELETE
        );

        //----MPA-----
        // Mpa id=1 get
        this.template("mpa/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"G\"\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Mpa  id=9999 get not found
        this.template("mpa/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        // Mpa  get All
        this.template("mpa",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"G\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"PG\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": \"PG-13\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 4,\n" +
                        "\t\t\"name\": \"R\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": \"NC-17\"\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----GENRE-----
        // Genre id=1 get
        this.template("genres/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Комедия\"\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Genre get unknown
        this.template("genres/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );

        // Genre All
        this.template("genres",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"Комедия\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"Драма\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": \"Мультфильм\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 4,\n" +
                        "\t\t\"name\": \"Триллер\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": \"Документальный\"\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 6,\n" +
                        "\t\t\"name\": \"Боевик\"\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Film id=1 update genre
        this.template("films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"genres\": [{ \"id\": 2}]\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        // Film id=1 get with genre
        this.template("films/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": \"NC-17\"\n" +
                        "\t}\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Film All with genre
        this.template("films",
                "",
                "[\n" +
                        "\t{\n" +
                        "\t\t\"id\": 1,\n" +
                        "\t\t\"name\": \"Film Updated\",\n" +
                        "\t\t\"description\": \"New film update decription\",\n" +
                        "\t\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\t\"duration\": 190,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\": 2,\n" +
                        "\t\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t\t}\n" +
                        "\t\t],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 5,\n" +
                        "\t\t\t\"name\": \"NC-17\"\n" +
                        "\t\t}\n" +
                        "\t},\n" +
                        "\t{\n" +
                        "\t\t\"id\": 2,\n" +
                        "\t\t\"name\": \"New film\",\n" +
                        "\t\t\"description\": \"New film about friends\",\n" +
                        "\t\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\t\"duration\": 120,\n" +
                        "\t\t\"rate\": 4,\n" +
                        "\t\t\"genres\": [\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\": 1,\n" +
                        "\t\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t\t}\n" +
                        "\t\t],\n" +
                        "\t\t\"mpa\": {\n" +
                        "\t\t\t\"id\": 3,\n" +
                        "\t\t\t\"name\": \"PG-13\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Film id=1 update remove  genre
        this.template("films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"genres\": []\n" +
                        "}",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        // Film id=1 get without genre
        this.template("films/1",
                "",
                "{\n" +
                        "\t\"id\": 1,\n" +
                        "\t\"name\": \"Film Updated\",\n" +
                        "\t\"description\": \"New film update decription\",\n" +
                        "\t\"releaseDate\": \"1989-04-17\",\n" +
                        "\t\"duration\": 190,\n" +
                        "\t\"rate\": 4,\n" +
                        "\t\"genres\": [],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 5,\n" +
                        "\t\t\"name\": \"NC-17\"\n" +
                        "\t}\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Film id=2 genres update
        this.template("films",
                "{\n" +
                        "  \"id\": 2,\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 3}]\n" +
                        "}\n",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"name\": \"New film\",\n" +
                        "\t\"description\": \"New film about friends\",\n" +
                        "\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\"duration\": 120,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 1,\n" +
                        "\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 3,\n" +
                        "\t\t\t\"name\": \"Мультфильм\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        // Film id=2 genres update
        this.template("films/2",
                "",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"name\": \"New film\",\n" +
                        "\t\"description\": \"New film about friends\",\n" +
                        "\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\"duration\": 120,\n" +
                        "\t\"rate\": 0,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 1,\n" +
                        "\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 3,\n" +
                        "\t\t\t\"name\": \"Мультфильм\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": \"PG-13\"\n" +
                        "\t}\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        // Film id=2  genres update with duplicate
        this.template("films",
                "{\n" +
                        "  \"id\": 2,\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 1}]\n" +
                        "}\n",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"name\": \"New film\",\n" +
                        "\t\"description\": \"New film about friends\",\n" +
                        "\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\"duration\": 120,\n" +
                        "\t\"rate\": null,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 1,\n" +
                        "\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": null\n" +
                        "\t}\n" +
                        "}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        // Film id=2  get with genre  without duplicate
        this.template("films/2",
                "",
                "{\n" +
                        "\t\"id\": 2,\n" +
                        "\t\"name\": \"New film\",\n" +
                        "\t\"description\": \"New film about friends\",\n" +
                        "\t\"releaseDate\": \"1999-04-30\",\n" +
                        "\t\"duration\": 120,\n" +
                        "\t\"rate\": 0,\n" +
                        "\t\"genres\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 1,\n" +
                        "\t\t\t\"name\": \"Комедия\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2,\n" +
                        "\t\t\t\"name\": \"Драма\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"mpa\": {\n" +
                        "\t\t\"id\": 3,\n" +
                        "\t\t\"name\": \"PG-13\"\n" +
                        "\t}\n" +
                        "}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        checkDb();
    }
}
