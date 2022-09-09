package school.hei.haapi.endpoint.rest.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.haapi.model.Place;

@Component
@AllArgsConstructor
public class PlaceMapper {

    public Place toRest(Place place){
       Place place1 = new Place();

       place1.setName(place.getName());
       return place1;
    }
}
