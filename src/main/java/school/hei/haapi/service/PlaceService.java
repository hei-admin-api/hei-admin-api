package school.hei.haapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.haapi.model.Place;
import school.hei.haapi.repository.PlaceRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<Place> getAllPlaces(){
        return placeRepository.findAll();
    }

    public Place getPlacesById(String id){
        return placeRepository.getById(id);
    }

    public Place getPlaceByName(String name){
        return placeRepository.findByNameContainingIgnoreCase(name);
    }
}
