package school.hei.haapi.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.hei.haapi.SentryConf;
import school.hei.haapi.endpoint.rest.api.TeachingApi;
import school.hei.haapi.endpoint.rest.api.UsersApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.model.Course;
import school.hei.haapi.endpoint.rest.model.Course;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.integration.conf.TestUtils.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = GroupIT.ContextInitializer.class)
@AutoConfigureMockMvc
public class CourseIT {
    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;
    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, TeacherIT.ContextInitializer.SERVER_PORT);
    }
    public static Course course1(){
        Course course = new Course();
        course.setId("1");
        course.setName("PROG");
        course.setRef("Algo");
        course.setCredits(10);
        course.setTotalHours(100);
        return course;
    }
    public Course course2(){
        Course course = new Course();
        course.setId("2");
        course.setName("WEB");
        course.setRef("IHM");
        course.setCredits(20);
        course.setTotalHours(100);
        return course;
    }
    public Course course3(){
        Course course = new Course();
        course.setId("3");
        course.setName("SYS");
        course.setRef("AWS");
        course.setCredits(10);
        course.setTotalHours(100);
        return course;
    }
    public static Course someCreatableCourse() {
        Course course = new Course();
        course.setId("4");
        course.setName("SYS2");
        course.setRef("Réseaux");
        course.setCredits(10);
        course.setTotalHours(100);
        return course;
    }
    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
    }
    @Test
    void badtoken_read_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        TeachingApi api = new TeachingApi(anonymousClient);
        assertThrowsForbiddenException(api::getGroups);
    }
    @Test
    void badtoken_write_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        TeachingApi api = new TeachingApi(anonymousClient);
        assertThrowsForbiddenException(() -> api.createOrUpdateGroups(List.of()));
    }
    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        TeachingApi api = new TeachingApi(student1Client);
        Course actual1 = api.getCourseById(COURSE1_ID);
        List<Course> actualGroups = api.getCourses();

        assertEquals(course1(), actual1);
        assertTrue(actualGroups.contains(course1()));
        assertTrue(actualGroups.contains(course2()));
    }

    @Test
    void student_write_ko() {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);


        TeachingApi api = new TeachingApi(student1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdateCourses(course2()));
    }

    @Test
    void teacher_write_ko() {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        TeachingApi api = new TeachingApi(teacher1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdateGroups(List.of()));
    }

    @Test
    void manager_write_create_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        Course toCreate3 = someCreatableCourse();
        Course toCreate4 = someCreatableCourse();

        TeachingApi api = new TeachingApi(manager1Client);
        Course created = api.createOrUpdateCourses(toCreate4);

        assertTrue(isValidUUID(toCreate3.getId()));
        toCreate3.setId(toCreate3.getId());
        assertNotNull(toCreate3.getTotalHours());
    }

//    @Test
//    void manager_write_update_ok() throws ApiException {
//        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
//        TeachingApi api = new TeachingApi(manager1Client);
//        List<Course> toUpdate = api.createOrUpdateCourses(List.of(
//                someCreatableCourse(),
//                someCreatableCourse()));
//        Course toUpdate0 = toUpdate.get(0);
//        toUpdate0.setName("A new name zero");
//        Course toUpdate1 = toUpdate.get(1);
//        toUpdate1.setName("A new name one");
//
//        List<Course> updated = api.createOrUpdateCourses(toUpdate);
//
//        assertEquals(2, updated.size());
//        assertTrue(updated.contains(toUpdate0));
//        assertTrue(updated.contains(toUpdate1));
//    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
