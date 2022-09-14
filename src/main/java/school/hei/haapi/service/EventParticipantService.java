package school.hei.haapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.hei.haapi.model.EventParticipant;
import school.hei.haapi.repository.EventParticipantRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository repository;

    public EventParticipant getById(String eventParticipantId) {
        return repository.getById(eventParticipantId);
    }

    public List<EventParticipant> getAll() {
        return repository.findAll();
    }

    @Transactional
    public List<EventParticipant> saveAll(List<EventParticipant> eventParticipant) {
        return repository.saveAll(eventParticipant);
    }

    @Transactional
    public EventParticipant save(EventParticipant eventParticipant){
        return repository.save(eventParticipant);
    }

}
