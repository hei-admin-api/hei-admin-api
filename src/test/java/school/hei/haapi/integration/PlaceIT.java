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
import school.hei.haapi.endpoint.rest.api.EventApi;
import school.hei.haapi.endpoint.rest.api.TeachingApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.model.Group;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;
import school.hei.haapi.model.Place;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    public Place place1(){
        Place place = new Place();
        place.setId("place1_id");
        place.setName("Ivandry");

        return place;
    }
    public static Place place2(){
        Place place = new Place();
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

        EventApi api = new EventApi(anonymousClient);
        assertThrowsForbiddenException(api::getPlaces);
    }

    @Test
    void badtoken_write_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        EventApi api = new EventApi(anonymousClient);
        assertThrowsForbiddenException(() -> api.createOrUpdatePlaces(new school.hei.haapi.endpoint.rest.model.Place()));
    }

    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

       EventApi api = new EventApi(student1Client);
        school.hei.haapi.endpoint.rest.model.Place actual1 = api.getPlaceById(PLACE1_ID);
        List<school.hei.haapi.endpoint.rest.model.Place> actualPlace = api.getPlaces();

        assertEquals(place1(), actual1);
        assertTrue(actualPlace.contains(place1()));
        assertTrue(actualPlace.contains(place2()));
    }

    @Test
    void student_write_ko() {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        EventApi api = new EventApi(student1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdatePlaces(someCreatablePlace()));
    }

    @Test
    void teacher_write_ko() {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        EventApi api = new EventApi(teacher1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdatePlaces(someCreatablePlace()));
    }

    @Test
    void manager_write_create_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        school.hei.haapi.endpoint.rest.model.Place toCreate3 = someCreatablePlace();
        school.hei.haapi.endpoint.rest.model.Place toCreate4 = someCreatablePlace();

        EventApi api = new EventApi(manager1Client);
        List<Place> created = api.createOrUpdatePlacesWithHttpInfo(List.of(toCreate3,toCreate4));

        assertEquals(2, created.size());
        Place created3 = created.get(0);
        assertTrue(isValidUUID(created3.getId()));
        toCreate3.setId(created3.getId());

        //
        assertEquals(created3, toCreate3);
        Place created4 = created.get(0);
        assertTrue(isValidUUID(created4.getId()));
        toCreate4.setId(created4.getId());
        assertEquals(created4, toCreate3);
    }

    @Test
    void manager_write_update_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        TeachingApi api = new TeachingApi(manager1Client);
        List<Group> toUpdate = api.createOrUpdateGroups(List.of(
                someCreatableGroup(),
                someCreatableGroup()));
        Group toUpdate0 = toUpdate.get(0);
        toUpdate0.setName("A new name zero");
        Group toUpdate1 = toUpdate.get(1);
        toUpdate1.setName("A new name one");

        List<Group> updated = api.createOrUpdateGroups(toUpdate);

        assertEquals(2, updated.size());
        assertTrue(updated.contains(toUpdate0));
        assertTrue(updated.contains(toUpdate1));
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
