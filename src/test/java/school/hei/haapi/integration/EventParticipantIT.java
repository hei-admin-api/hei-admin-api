package school.hei.haapi.integration;

import com.github.javafaker.Faker;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.hei.haapi.SentryConf;
import school.hei.haapi.endpoint.rest.api.PlaceApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.model.EnableStatus;
import school.hei.haapi.endpoint.rest.model.EventParticipant;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.endpoint.rest.model.EventParticipant.StatusEnum.*;
import static school.hei.haapi.integration.conf.TestUtils.MANAGER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.STUDENT1_ID;
import static school.hei.haapi.integration.conf.TestUtils.STUDENT1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.anAvailableRandomPort;
import static school.hei.haapi.integration.conf.TestUtils.assertThrowsApiException;
import static school.hei.haapi.integration.conf.TestUtils.assertThrowsForbiddenException;
import static school.hei.haapi.integration.conf.TestUtils.setUpCognito;
import static school.hei.haapi.integration.conf.TestUtils.setUpEventBridge;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = EventParticipantIT.ContextInitializer.class)
@AutoConfigureMockMvc
class EventParticipantIT {

    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;

    @MockBean
    private EventBridgeClient eventBridgeClientMock;

    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
    }

    public static school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipant1() {
        school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipant = new school.hei.haapi.endpoint.rest.model.EventParticipant();
        eventParticipant.setId("eventParticipant1_id");
        eventParticipant.setUser(StudentIT.student1());
        eventParticipant.setStatus(EXPECTED);
        return eventParticipant;
    }

    public static school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipant2() {
        school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipant = new school.hei.haapi.endpoint.rest.model.EventParticipant();
        eventParticipant.setId("eventParticipant2_id");
        eventParticipant.setUser(StudentIT.student2());
        eventParticipant.setStatus(MISSING);
        return eventParticipant;
    }


    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
        setUpEventBridge(eventBridgeClientMock);
    }

    @Test
    void student_read_own_ok() throws ApiException {
        ApiClient eventParticipant1Client = anApiClient(STUDENT1_TOKEN);

        PlaceApi api = new PlaceApi(eventParticipant1Client);
        school.hei.haapi.endpoint.rest.model.EventParticipant actual = api.getEventParticipantById("event1_id","eventParticipant1_id");

        assertEquals(eventParticipant1(), actual);
    }

    @Test
    void student_read_ko() {
        ApiClient eventParticipant1Client = anApiClient(STUDENT1_TOKEN);
        PlaceApi api = new PlaceApi(eventParticipant1Client);

        assertThrowsForbiddenException(() -> api.getEventParticipantById("event1_id","eventParticipant1_id"));

        assertThrowsForbiddenException(
                () -> api.getEventParticipants("event1_id"));
    }

    @Test
    void teacher_read_ok() throws ApiException {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);
        PlaceApi api = new PlaceApi(teacher1Client);
        school.hei.haapi.endpoint.rest.model.EventParticipant actualEventParticipant1 = api.getEventParticipantById("event1_id","eventParticipant1_id");

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> actualEventParticipants = api.getEventParticipants("event1_id");

        assertEquals(eventParticipant1(), actualEventParticipant1);
        assertTrue(actualEventParticipants.contains(eventParticipant1()));
        assertTrue(actualEventParticipants.contains(eventParticipant2()));
    }

    @Test
    void student_write_ko() {
        ApiClient eventParticipant1Client = anApiClient(STUDENT1_TOKEN);
        PlaceApi api = new PlaceApi(eventParticipant1Client);

        assertThrowsForbiddenException(() ->
    }

    @Test
    void teacher_write_ko() {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);
        PlaceApi api = new PlaceApi(teacher1Client);

        assertThrowsForbiddenException(() -> api.get(List.of()));
    }

    @Test
    void manager_read_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> actualEventParticipants = api.getEventParticipants(1, 20, null, null, null);

        assertTrue(actualEventParticipants.contains(eventParticipant1()));
        assertTrue(actualEventParticipants.contains(eventParticipant2()));
    }

    @Test
    void manager_read_by_ref_ignoring_case_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> actualEventParticipants = api.getEventParticipants(1, 20, "std21001", null, null);

        assertEquals("eventParticipant1_id", eventParticipant1().getId());
        assertEquals(1, actualEventParticipants.size());
        assertTrue(actualEventParticipants.contains(eventParticipant1()));
    }

    @Test
    void manager_read_by_ref_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> actualEventParticipants = api.getEventParticipants(1, 20, eventParticipant1().getRef(), null, null);

        assertEquals(1, actualEventParticipants.size());
        assertTrue(actualEventParticipants.contains(eventParticipant1()));
    }

    @Test
    void manager_read_by_last_name_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> actualEventParticipants = api.getEventParticipants(1, 20, null, null, eventParticipant2().getLastName());

        assertEquals(2, actualEventParticipants.size());
        assertTrue(actualEventParticipants.contains(eventParticipant2()));
        assertTrue(actualEventParticipants.contains(eventParticipant1()));
    }


    @Test
    void manager_write_update_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);
        List<school.hei.haapi.endpoint.rest.model.EventParticipant> toUpdate =
                api.UpdateEventParticipant(List.of(school.hei.haapi.endpoint.rest.model.EventParticipant(), school.hei.haapi.endpoint.rest.model.EventParticipant()));
        school.hei.haapi.endpoint.rest.model.EventParticipant toUpdate0 = toUpdate.get(0);
        toUpdate0.setLastName("A new name zero");
        school.hei.haapi.endpoint.rest.model.EventParticipant toUpdate1 = toUpdate.get(1);
        toUpdate1.setLastName("A new name one");

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> updated = api.UpdateR(toUpdate);

        assertEquals(2, updated.size());
        assertTrue(updated.contains(toUpdate0));
        assertTrue(updated.contains(toUpdate1));
    }

    /*
    * @Test
    void manager_write_update_triggers_userUpserted() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        PlaceApi api = new PlaceApi(manager1Client);
        reset(eventBridgeClientMock);
        when(eventBridgeClientMock.putEvents((PutEventsRequest) any())).thenReturn(
                PutEventsResponse.builder().entries(
                                PutEventsResultEntry.builder().eventId("eventId1").build(),
                                PutEventsResultEntry.builder().eventId("eventId2").build())
                        .build());

        List<school.hei.haapi.endpoint.rest.model.EventParticipant> created =
                api.UpdateEventParticipant(List.of(school.hei.haapi.endpoint.rest.model.EventParticipant(), school.hei.haapi.endpoint.rest.model.EventParticipant()));

        ArgumentCaptor<PutEventsRequest> captor = ArgumentCaptor.forClass(PutEventsRequest.class);
        verify(eventBridgeClientMock, times(1)).putEvents(captor.capture());
        PutEventsRequest actualRequest = captor.getValue();
        List<PutEventsRequestEntry> actualRequestEntries = actualRequest.entries();
        assertEquals(2, actualRequestEntries.size());
        school.hei.haapi.endpoint.rest.model.EventParticipant created0 = created.get(0);
        PutEventsRequestEntry requestEntry0 = actualRequestEntries.get(0);
        assertTrue(requestEntry0.detail().contains(created0.getId()));
        assertTrue(requestEntry0.detail().contains(created0.getEmail()));
        school.hei.haapi.endpoint.rest.model.EventParticipant created1 = created.get(1);
        PutEventsRequestEntry requestEntry1 = actualRequestEntries.get(1);
        assertTrue(requestEntry1.detail().contains(created1.getId()));
        assertTrue(requestEntry1.detail().contains(created1.getEmail()));
    }*/

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
