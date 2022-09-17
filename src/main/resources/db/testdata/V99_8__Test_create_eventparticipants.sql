insert into "event_participant"
    (id,user_id, ref, credits, total_hours)
        values ('eventParticipant1_id','PROG','Algo', 5, 20),
               ('eventParticipant2_id','WEB','IHM', 5, 20);

  EventParticipant eventParticipant = new school.hei.haapi.endpoint.rest.model.EventParticipant();
           eventParticipant.setId("eventParticipant1_id");
           eventParticipant.setUser(new User());
           eventParticipant.setEvent(EventIT.event1());
           eventParticipant.setStatus(EXPECTED);

           school.hei.haapi.endpoint.rest.model.EventParticipant eventParticipant = new school.hei.haapi.endpoint.rest.model.EventParticipant();
                   eventParticipant.setId("eventParticipant2_id");
                   eventParticipant.setUser(new User());
                   eventParticipant.setEvent(EventIT.event2());
                   eventParticipant.setStatus(MISSING);
                   return eventParticipant;
