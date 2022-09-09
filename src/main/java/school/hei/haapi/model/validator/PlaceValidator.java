package school.hei.haapi.model.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import school.hei.haapi.model.Fee;
import school.hei.haapi.model.Place;
import school.hei.haapi.model.exception.BadRequestException;

@Component
public class PlaceValidator implements Consumer<Place> {


    @Override
    public void accept(Place place) {
        Set<String> violationMessage = new HashSet<>();

        if(place.getName()==null){
            violationMessage.add("Place is mandatory");
        }
        if (!violationMessage.isEmpty()) {
            String formattedViolationMessages = violationMessage.stream()
                    .map(String::toString)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException(formattedViolationMessages);
        }
    }

}
