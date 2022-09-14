package school.hei.haapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.hei.haapi.model.Event;
import school.hei.haapi.model.EventParticipant;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
    void deleteById(String id);

    @Query(value = "select p from EventParticipant p join Event e on e.id = p.id"
            + " where e.id = :event_id")
    List<EventParticipant> getEventParticipants(
            @Param("event_id") String eventId);
}
