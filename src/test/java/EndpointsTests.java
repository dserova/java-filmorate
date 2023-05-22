import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filorate.FilmorateApplication;
import ru.yandex.practicum.filorate.exception.ValidationException;

import java.io.IOException;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Test
    void caseTests() throws IOException {
        //----USERS-----
        // User create
        this.template("/users",
                "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}",
                "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // User create Fail login
        this.template("/users",
                "{\n  \"login\": \"dolore ullamco\",\n  \"email\": \"yandex@mail.ru\",\n  \"birthday\": \"2446-08-20\"\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User create Fail email
        this.template("/users",
                "{\n  \"login\": \"dolore ullamco\",\n  \"name\": \"\",\n  \"email\": \"mail.ru\",\n  \"birthday\": \"1980-08-20\"\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User create Fail birthday
        this.template("/users",
                "{\n  \"login\": \"dolore\",\n  \"name\": \"\",\n  \"email\": \"test@mail.ru\",\n  \"birthday\": \"2446-08-20\"\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // User update
        this.template("/users",
                "{\n  \"login\": \"doloreUpdate\",\n  \"name\": \"est adipisicing\",\n  \"id\": 1,\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"1976-09-20\"\n}",
                "{\n  \"login\": \"doloreUpdate\",\n  \"name\": \"est adipisicing\",\n  \"id\": 1,\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"1976-09-20\"\n}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // User update unknown
        this.template("/users",
                "{\n  \"login\": \"doloreUpdate\",\n  \"name\": \"est adipisicing\",\n  \"id\": 9999,\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"1976-09-20\"\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );

        // User get All
        this.template("/users",
                "",
                "[{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\",\"friends\":[],\"filmsLikes\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FRIENDS-----
        // Friend Create
        this.template("/users",
                "{\n  \"login\": \"friend\",\n  \"name\": \"friend adipisicing\",\n  \"email\": \"friend@mail.ru\",\n  \"birthday\": \"1976-08-20\"\n}",
                "{\n  \"login\": \"friend\",\n  \"name\": \"friend adipisicing\",\n  \"email\": \"friend@mail.ru\",\n  \"birthday\": \"1976-08-20\"\n}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Common Friend Create
        this.template("/users",
                "{\n  \"login\": \"common\",\n  \"name\": \"\",\n  \"email\": \"friend@common.ru\",\n  \"birthday\": \"2000-08-20\"\n}",
                "{\n  \"login\": \"common\",\n  \"name\": \"common\",\n  \"email\": \"friend@common.ru\",\n  \"birthday\": \"2000-08-20\"\n}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // User get by id=1
        this.template("/users/1",
                "",
                "{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\",\"friends\":[],\"filmsLikes\":[]}",
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
                "{\n  \"login\": \"friend\",\n  \"name\": \"friend adipisicing\",\n  \"email\": \"friend@mail.ru\",\n  \"birthday\": \"1976-08-20\"\n}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

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
                "[{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\",\"friends\":[1],\"filmsLikes\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=2 get friends
        this.template("users/2/friends",
                "",
                "[{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\",\"friends\":[2],\"filmsLikes\":[]}]",
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
                "[{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\",\"friends\":[1],\"filmsLikes\":[]},{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\",\"friends\":[1],\"filmsLikes\":[]}]",
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

        // User id=2 get 2 friends
        this.template("users/2/friends",
                "",
                "[{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\",\"friends\":[2,3],\"filmsLikes\":[]},{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\",\"friends\":[1,2],\"filmsLikes\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Common friend to user id=1 with user id=2
        this.template("users/1/friends/common/2",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\",\"friends\":[1,2],\"filmsLikes\":[]}]",
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
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\",\"friends\":[1,2],\"filmsLikes\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // User id=2 get common with user id=1
        this.template("users/2/friends/common/1",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\",\"friends\":[1,2],\"filmsLikes\":[]}]",
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
                "{\n  \"name\": \"nisi eiusmod\",\n  \"description\": \"adipisicing\",\n  \"releaseDate\": \"1967-03-25\",\n  \"duration\": 100\n}",
                "{\"id\":1,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100,\"rate\":null}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Film create Fail name
        this.template("/films",
                "{\n  \"name\": \"\",\n  \"description\": \"Description\",\n  \"releaseDate\": \"1900-03-25\",\n  \"duration\": 200\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail description
        this.template("/films",
                "{\n  \"name\": \"Film name\",\n  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n    \"releaseDate\": \"1900-03-25\",\n  \"duration\": 200\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail releaseDate
        this.template("/films",
                "{\n  \"name\": \"Name\",\n  \"description\": \"Description\",\n  \"releaseDate\": \"1890-03-25\",\n  \"duration\": 200\n}",
                "{\"id\":0,\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"1890-03-25\",\"duration\":200,\"rate\":null}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film create Fail duration
        this.template("/films",
                "{\n  \"name\": \"Name\",\n  \"description\": \"Descrition\",\n  \"releaseDate\": \"1980-03-25\",\n  \"duration\": -200\n}",
                "{\"id\":0,\"name\":\"Name\",\"description\":\"Descrition\",\"releaseDate\":\"1980-03-25\",\"duration\":-200,\"rate\":null}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );

        // Film update
        this.template("/films",
                "{\n  \"id\": 1,\n  \"name\": \"Film Updated\",\n  \"releaseDate\": \"1989-04-17\",\n  \"description\": \"New film update decription\",\n  \"duration\": 190,\n  \"rate\": 4\n}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"rate\":4}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );

        // Film update unknown
        this.template("/films",
                "{\n  \"id\": 9999,\n  \"name\": \"Film Updated\",\n  \"releaseDate\": \"1989-04-17\",\n  \"description\": \"New film update decription\",\n  \"duration\": 190,\n  \"rate\": 4\n}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );

        // Film get All
        this.template("/films",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"rate\":4}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        // Film get Popular
        this.template("/films/popular",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"rate\":4}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        //----FILM-----
        // Film id=2 create
        this.template("/films",
                "{\n  \"name\": \"New film\",\n  \"releaseDate\": \"1999-04-30\",\n  \"description\": \"New film about friends\",\n  \"duration\": 120,\n  \"rate\": 4\n}",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"rate\":4}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );

        // Film id=1 get
        this.template("/films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"rate\":4}",
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
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"rate\":5}]",
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
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"rate\":5},{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"rate\":4}]",
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
    }
}
