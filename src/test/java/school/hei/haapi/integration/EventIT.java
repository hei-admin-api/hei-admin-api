package school.hei.haapi.integration;

import java.time.Instant;
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
import school.hei.haapi.endpoint.rest.model.Event;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.integration.conf.TestUtils.BAD_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.GROUP1_ID;
import static school.hei.haapi.integration.conf.TestUtils.MANAGER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.STUDENT1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.anAvailableRandomPort;
import static school.hei.haapi.integration.conf.TestUtils.assertThrowsForbiddenException;
import static school.hei.haapi.integration.conf.TestUtils.isValidUUID;
import static school.hei.haapi.integration.conf.TestUtils.setUpCognito;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = EventIT.ContextInitializer.class)
@AutoConfigureMockMvc
public class EventIT {

    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;

    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
    }

    public static Event event1() {
        Event event = new Event();
        event.setId("event1_id");
        event.setName("Name of event one");
        event.setPlace(PlaceIT.place1());
        event.setStartDate(Instant.parse("2022-09-08T08:00:00.00Z"));
        event.setEndDate(Instant.parse("2022-09-08T10:00:00.00Z"));
        event.setEventParticipants(List.of(EventParticipantIT.eventParticipant1(),EventParticipantIT.eventParticipant2()));
        return event;
    }

    public static Event event2() {
        Event event = new Event();
        event.setId("event2_id");
        event.setName("Name of event two");
        event.setPlace(PlaceIT.place2());
        event.setStartDate(Instant.parse("2022-09-08T08:00:00.00Z"));
        event.setEndDate(Instant.parse("2022-09-08T10:00:00.00Z"));
        event.setEventParticipants(List.of(EventParticipantIT.eventParticipant1(),EventParticipantIT.eventParticipant2()));
        return event;
    }

    public static Event someCreatableEvent() {
        Event event = new Event();
        Faker faker = new Faker();
        event.setName("Event_"+ faker.number().toString());
        event.setEndDate(Instant.parse(faker.date().toString()));
        event.setStartDate(Instant.parse(faker.date().toString()));
        event.setPlace(PlaceIT.someCreatablePlace());
        event.setEventParticipants(List.of(EventParticipantIT.someCreatableEventParticipant(),
                EventParticipantIT.someCreatableEventParticipant()));
        return event;
    }

    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
    }

    @Test
    void badtoken_read_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        PlaceApi api = new PlaceApi(anonymousClient);
        assertThrowsForbiddenException(api::getEvents);
    }

    @Test
    void badtoken_write_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        PlaceApi api = new PlaceApi(anonymousClient);
        assertThrowsForbiddenException(() -> api.createOrUpdateEvents(List.of()));
    }

    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        Event actual1 = api.getEventById(GROUP1_ID);
        List<Event> actualEvents = api.getEvents();

        assertEquals(event1(), actual1);
        assertTrue(actualEvents.contains(event1()));
        assertTrue(actualEvents.contains(event2()));
    }

    @Test
    void student_write_ko() {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdateEvents(List.of()));
    }

    @Test
    void teacher_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(TEACHER1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        Event actual1 = api.getEventById(GROUP1_ID);
        List<Event> actualEvents = api.getEvents();

        assertEquals(event1(), actual1);
        assertTrue(actualEvents.contains(event1()));
        assertTrue(actualEvents.contains(event2()));
    }

    @Test
    void teacher_write_ko() {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        PlaceApi api = new PlaceApi(teacher1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdateEvents(List.of()));
    }

    @Test
    void manager_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(MANAGER1_TOKEN);

        PlaceApi api = new PlaceApi(student1Client);
        Event actual1 = api.getEventById(GROUP1_ID);
        List<Event> actualEvents = api.getEvents();

        assertEquals(event1(), actual1);
        assertTrue(actualEvents.contains(event1()));
        assertTrue(actualEvents.contains(event2()));
    }
    @Test
    void manager_write_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        Event toCreate3 = someCreatableEvent();
        Event toCreate4 = someCreatableEvent();

        PlaceApi api = new PlaceApi(manager1Client);
        List<Event> created = api.createOrUpdateEvents(List.of(toCreate3, toCreate4));

        assertEquals(2, created.size());
        Event created3 = created.get(0);
        assertNotNull(created3.getId());
        toCreate3.setId(created3.getId());
        //
        assertEquals(created3, toCreate3);
        Event created4 = created.get(0);
        toCreate4.setId(created4.getId());
        assertEquals(created4, toCreate3);
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
