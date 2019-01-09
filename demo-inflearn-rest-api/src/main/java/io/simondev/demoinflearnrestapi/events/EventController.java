package io.simondev.demoinflearnrestapi.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
// Base URL은 /api/events
// HAL JSON 타입으로 응답을 보낸다.
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private EventRepository eventRepository;

    // 생성자가 하나이고, 생성자로 받아올 파라미터가 이미 빈으로 등록되있다면
    // 생성자 위에 @Autowired라는 애노테이션은 생략이 가능하다.
    // 스프링 4.3부터...
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {
        Event newEvent = eventRepository.save(event);

        // HATEOAS에서 제공
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();

        // Body에 이벤트를 담아서 리턴
        return ResponseEntity.created(createdUri).body(event);
    }
}
