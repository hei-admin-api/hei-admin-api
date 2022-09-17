package school.hei.haapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.hei.haapi.model.EventParticipant;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, String> {

    @Query(value = "update EventParticipant set status=stat"
            + " where id = :eventParticipant_id")
    EventParticipant setStatus(@Param("eventParticipant_id") String eventParticipantId, @Param("status") String stat);
}
