package school.hei.haapi.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.hei.haapi.endpoint.rest.mapper.PlaceMapper;
import school.hei.haapi.service.PlaceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin
public class PlaceController {
    private PlaceService placeService;

    private PlaceMapper placeMapper;

    @GetMapping("/places")
    public List<school.hei.haapi.endpoint.rest.model.Place> getAllPlace(){
        return placeService.getAllPlaces()
                .stream()
                .map(place -> placeMapper.toRest(place))
                .collect(Collectors.toList());
    }

    @GetMapping("/places/{id}")
    public school.hei.haapi.endpoint.rest.model.Place getPlaceById(@PathVariable String id){
        return placeMapper.toRest(placeService.getPlacesById(id));
    }
}

