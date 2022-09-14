package school.hei.haapi.integration;

import java.util.List;
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

    public static school.hei.haapi.endpoint.rest.model.Place place1(){
        school.hei.haapi.endpoint.rest.model.Place place = new school.hei.haapi.endpoint.rest.model.Place();
        place.setId("place1_id");
        place.setName("Ivandry");

        return place;
    }
    public static school.hei.haapi.endpoint.rest.model.Place place2(){
        school.hei.haapi.endpoint.rest.model.Place place = new Place();
        place.setId("place2_id");
        place.setName("Alliance française Andavamamba");
        return place;
    }

    public  static school.hei.haapi.endpoint.rest.model.Place someCreatablePlace() {
        school.hei.haapi.endpoint.rest.model.Place place1= new school.hei.haapi.endpoint.rest.model.Place();
        place1.setName("Some name");
        place1.setId("LOC21-" + randomUUID());
        return place1;
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
    /*
    @Test
    void manager_write_create_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        school.hei.haapi.endpoint.rest.model.Place toCreate3 = someCreatablePlace();
        school.hei.haapi.endpoint.rest.model.Place toCreate4 = someCreatablePlace();

        PlaceApi api = new PlaceApi(manager1Client);
        List<school.hei.haapi.endpoint.rest.model.Place> created = (List<school.hei.haapi.endpoint.rest.model.Place>) api.createOrUpdatePlacesWithHttpInfo(List.of(toCreate3,toCreate4));

        assertEquals(2, created.size());
        school.hei.haapi.endpoint.rest.model.Place created3 = created.get(0);
        assertTrue(isValidUUID(created3.getId()));
        toCreate3.setId(created3.getId());

        //
        assertEquals(created3, toCreate3);
        school.hei.haapi.endpoint.rest.model.Place created4 = created.get(0);
        assertTrue(isValidUUID(created4.getId()));
        toCreate4.setId(created4.getId());
        assertEquals(created4, toCreate3);
    }
    /*
    @Test
    void manager_write_update_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);
        List<school.hei.haapi.endpoint.rest.model.Place> toUpdate = (List<school.hei.haapi.endpoint.rest.model.Place>) api.createOrUpdatePlaces(List.of(
                someCreatablePlace(),
                someCreatablePlace()));
        school.hei.haapi.endpoint.rest.model.Place toUpdate0 = toUpdate.get(0);
        toUpdate0.setName("A new name zero");
        school.hei.haapi.endpoint.rest.model.Place toUpdate1 = toUpdate.get(1);
        toUpdate1.setName("A new name one");

        List<Place> updated = (List<Place>) api.createOrUpdatePlaces(toUpdate);

        assertEquals(2, updated.size());
        assertTrue(updated.contains(toUpdate0));
        assertTrue(updated.contains(toUpdate1));
    }
    */
    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
