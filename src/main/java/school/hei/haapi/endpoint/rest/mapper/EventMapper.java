package school.hei.haapi.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import school.hei.haapi.endpoint.rest.model.EventParticipant;

@Component
public class EventMapper {
    public school.hei.haapi.endpoint.rest.model.Event toRest(school.hei.haapi.model.Event event) {
        var restEvent = new school.hei.haapi.endpoint.rest.model.Event();
        restEvent.setId(event.getId());
        restEvent.setName(event.getName());
        restEvent.setStartDate(event.getStartDate());
        restEvent.setEndDate(event.getEndDate());
        return restEvent;
    }

    public school.hei.haapi.model.Event toDomain(school.hei.haapi.endpoint.rest.model.Event restEvent) {
        return school.hei.haapi.model.Event.builder()
                .id(restEvent.getId())
                .name(restEvent.getName())
                .startDate(restEvent.getStartDate())
                .endDate(restEvent.getEndDate())
                .build();
    }

    public school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipanttoRest(school.hei.haapi.model.EventParticipant eventParticipant) {
        var restEventParticipant = new school.hei.haapi.endpoint.rest.model.EventParticipant();
        restEventParticipant.setId(eventParticipant.getId());
        restEventParticipant.setStatus(EventParticipant.StatusEnum.fromValue(eventParticipant.getStatus().toString()));
        return restEventParticipant;
    }

    public school.hei.haapi.model.EventParticipant eventParticipanttoDomain(
            school.hei.haapi.endpoint.rest.model.EventParticipant restEventParticipant) {
        return school.hei.haapi.model.EventParticipant.builder()
                .id(restEventParticipant.getId())
                .status(school.hei.haapi.model.EventParticipant.Status.valueOf(restEventParticipant.getStatus().toString()))
                .build();
    }
}
