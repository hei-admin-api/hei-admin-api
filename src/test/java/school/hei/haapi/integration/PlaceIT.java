package school.hei.haapi.integration;

import java.util.List;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.hei.haapi.SentryConf;
import school.hei.haapi.endpoint.rest.api.PlaceApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.model.Place;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.integration.conf.TestUtils.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = GroupIT.ContextInitializer.class)
@AutoConfigureMockMvc
public class PlaceIT {

    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;

    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
    }

    public school.hei.haapi.endpoint.rest.model.Place place1(){
        school.hei.haapi.endpoint.rest.model.Place place = new school.hei.haapi.endpoint.rest.model.Place();
        place.setId("place1_id");
        place.setName("Ivandry");
        place.setAddress("II J 161 R Ambodivoanjo Ivandry Antananarivo, 101");

        return place;
    }
    public school.hei.haapi.endpoint.rest.model.Place place2(){
        school.hei.haapi.endpoint.rest.model.Place place = new school.hei.haapi.endpoint.rest.model.Place();
        place.setId("place2_id");
        place.setName("Alliance Française");
        place.setAddress("Andavamamba");

        return place;
    }

    public  static school.hei.haapi.endpoint.rest.model.Place someCreatablePlace() {
        Faker faker = new Faker();
        school.hei.haapi.endpoint.rest.model.Place place = new school.hei.haapi.endpoint.rest.model.Place();
        place.setName("Place_" + faker.number());
        place.setId("Place_" + randomUUID());
        place.setAddress(faker.address().toString());
        return place;
    }

    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
    }

    @Test
    void badtoken_read_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        PlaceApi api = new PlaceApi(anonymousClient);
        assertThrowsForbiddenException(api::getPlaces);
    }

    @Test
    void badtoken_write_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        PlaceApi api = new PlaceApi(anonymousClient);

        assertThrowsForbiddenException(api::getPlaces);
    }

    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

       PlaceApi api = new PlaceApi(student1Client);
        school.hei.haapi.endpoint.rest.model.Place actual1 = api.getPlaceById(PLACE1_ID);
        List<school.hei.haapi.endpoint.rest.model.Place> actualPlace = api.getPlaces();

        assertEquals(place1(), actual1);
        assertTrue(actualPlace.contains(place1()));
        assertTrue(actualPlace.contains(place2()));
    }

    @Test
    void student_write_ko() {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);

        assertThrowsForbiddenException(() -> api.createOrUpdatePlaces(List.of()));
    }

    @Test
    void teacher_write_ko() {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        PlaceApi api = new PlaceApi(teacher1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdatePlaces(List.of()));
    }

    @Test
    void teacher_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(TEACHER1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        school.hei.haapi.endpoint.rest.model.Place actual1 = api.getPlaceById(PLACE1_ID);
        List<school.hei.haapi.endpoint.rest.model.Place> actualPlace = api.getPlaces();

        assertEquals(place1(), actual1);
        assertTrue(actualPlace.contains(place1()));
        assertTrue(actualPlace.contains(place2()));
    }

    @Test
    void manager_write_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);

        PlaceApi api = new PlaceApi(manager1Client);
        List<Place> toUpdate =
                api.createOrUpdatePlaces(List.of(someCreatablePlace(), someCreatablePlace()));
        Place toUpdate0 = toUpdate.get(0);
        toUpdate0.setName("A new name zero");
        Place toUpdate1 = toUpdate.get(1);
        toUpdate1.setName("A new name one");
        List<Place> updated = api.createOrUpdatePlaces(toUpdate);

        assertEquals(2, updated.size());
        assertTrue(updated.contains(toUpdate0));
        assertTrue(updated.contains(toUpdate1));
    }

    @Test
    void manager_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(MANAGER1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        school.hei.haapi.endpoint.rest.model.Place actual1 = api.getPlaceById(PLACE1_ID);
        List<school.hei.haapi.endpoint.rest.model.Place> actualPlace = api.getPlaces();

        assertEquals(place1(), actual1);
        assertTrue(actualPlace.contains(place1()));
        assertTrue(actualPlace.contains(place2()));
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
