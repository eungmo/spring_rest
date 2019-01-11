package io.simondev.demoinflearnrestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.validation.Errors;
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

    private final EventValidator eventValidator;

    // 생성자가 하나이고, 생성자로 받아올 파라미터가 이미 빈으로 등록되있다면
    // 생성자 위에 @Autowired라는 애노테이션은 생략이 가능하다.
    // 스프링 4.3부터...
    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        // 맵핑 과정에서 에러가 발생했을 경우, Bad Request를 발생시킨다
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Validation 검증 과정에서 에러가 발생할 경우, 역시 Bad Request를 발생시킨다.
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            // 여기에 .body(errors)가 안된다. 왜냐하면 Event 클래스와 다르게 자바 빈 스펙을 따르지 않기 때문에...
            // 내부적으로는 ObjectMapper의 BeanSerializer를 사용하는데, 자바 빈 스펙을 준수하는 객체만 JSON으로 변환할 수 있다.
            // JSON으로 변환하려는 이유는 produces = MediaTypes.HAL_JSON_UTF8_VALUE를 명시
            // 했기 때문이다.
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class); // eventDto에 있는 내용을 Event 타입의 인스턴스로 만들어달라

        Event newEvent = eventRepository.save(event);

        // HATEOAS에서 제공
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();

        // Body에 이벤트를 담아서 리턴
        return ResponseEntity.created(createdUri).body(event);
    }
}
