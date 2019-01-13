package io.simondev.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class EventResource extends Resource<Event> {
    public EventResource(Event event, Link... links) {
        super(event, links);
        // 이와 같다 add(new Link("http://localhost:8080/api/events/" + event.getId()));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
