package school.hei.haapi.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.hei.haapi.endpoint.rest.mapper.EventMapper;
import school.hei.haapi.endpoint.rest.model.Event;
import school.hei.haapi.endpoint.rest.model.EventParticipant;
import school.hei.haapi.service.EventParticipantService;
import school.hei.haapi.service.EventService;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@RestController
@AllArgsConstructor
public class EventController {

  private final EventService eventService;
  private final EventMapper eventMapper;

  private final EventParticipantService eventParticipantService;

  @GetMapping(value = "/events")
  public List<Event> getEvents() {
    return eventService.getAll().stream()
        .map(eventMapper::toRest)
        .collect(toUnmodifiableList());
  }

  @PutMapping(value = "/events")
  public List<Event> createOrUpdateEvents(@RequestBody List<Event> toWrite) {
    var saved = eventService.saveAll(toWrite.stream()
        .map(eventMapper::toDomain)
        .collect(toUnmodifiableList()));
    return saved.stream()
        .map(eventMapper::toRest)
        .collect(toUnmodifiableList());
  }

  @GetMapping(value = "/events/{id}")
  public Event getEventById(@PathVariable String id) {
    return eventMapper.toRest(eventService.getById(id));
  }

  @DeleteMapping(value = "/events/{id}")
  public void deleteEventById(@PathVariable String id) {
    eventService.deleteById(id);
  }

  @GetMapping(value = "/events/{id}/eventParticipants")
  public List<EventParticipant> getEventParticipants(@PathVariable String id) {
    return eventService.getEventParticipants(id).stream()
            .map(eventMapper::eventParticipanttoRest)
            .collect(toUnmodifiableList());
  }

  @PutMapping(value = "/events/{event_id}/eventParticipants/")
  public List<EventParticipant> UpdateEventParticipants(
    @PathVariable String id, @RequestBody List<EventParticipant> toWrite) {
    var saved = eventParticipantService.saveAll(toWrite.stream()
            .map(eventMapper::eventParticipanttoDomain)
            .collect(toUnmodifiableList()));
    return saved.stream()
            .map(eventMapper::eventParticipanttoRest)
            .collect(toUnmodifiableList());
  }
}
