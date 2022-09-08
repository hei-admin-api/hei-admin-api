package school.hei.haapi.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.hei.haapi.endpoint.rest.mapper.CourseMapper;
import school.hei.haapi.endpoint.rest.mapper.GroupMapper;
import school.hei.haapi.endpoint.rest.model.Course;
import school.hei.haapi.endpoint.rest.model.Group;
import school.hei.haapi.service.CourseService;
import school.hei.haapi.service.GroupService;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@RestController
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    @GetMapping(value = "/course/{id}")
    public Course getCourseById(@PathVariable String id) {
        return courseMapper.toRest(courseService.getById(id));
    }

    @GetMapping(value = "/course")
    public List<Course> getGroups() {
        return courseService.getAll().stream()
                .map(courseMapper::toRest)
                .collect(toUnmodifiableList());
    }

    @PutMapping(value = "/course")
    public List<Course> createOrUpdateGroups(@RequestBody List<Course> toWrite) {
        var saved = courseService.saveAll(toWrite.stream()
                .map(courseMapper::toDomain)
                .collect(toUnmodifiableList()));
        return saved.stream()
                .map(courseMapper::toRest)
                .collect(toUnmodifiableList());
    }
}
