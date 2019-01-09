package io.simondev.demoinflearnrestapi.events;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
// Base URL은 /api/events
// HAL JSON 타입으로 응답을 보낸다.
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    // 생성자가 하나이고, 생성자로 받아올 파라미터가 이미 빈으로 등록되있다면
    // 생성자 위에 @Autowired라는 애노테이션은 생략이 가능하다.
    // 스프링 4.3부터...
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors erros) {
        // 맵핑 과정에서 에러가 발생했을 경우, Bad Request를 발생시킨다
        if (erros.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Event event = modelMapper.map(eventDto, Event.class); // eventDto에 있는 내용을 Event 타입의 인스턴스로 만들어달라

        Event newEvent = eventRepository.save(event);

        // HATEOAS에서 제공
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();

        // Body에 이벤트를 담아서 리턴
        return ResponseEntity.created(createdUri).body(event);
    }
}
