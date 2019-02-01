package io.simondev.demoinflearnrestapi.events;

import io.simondev.demoinflearnrestapi.common.ErrorsResource;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
            return badRequest(errors);
        }

        // Validation 검증 과정에서 에러가 발생할 경우, 역시 Bad Request를 발생시킨다.
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            // 여기에 .body(errors)가 안된다. 왜냐하면 Event 클래스와 다르게 자바 빈 스펙을 따르지 않기 때문에...
            // 내부적으로는 ObjectMapper의 BeanSerializer를 사용하는데, 자바 빈 스펙을 준수하는 객체만 JSON으로 변환할 수 있다.
            // JSON으로 변환하려는 이유는 produces = MediaTypes.HAL_JSON_UTF8_VALUE를 명시
            // 했기 때문이다.
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class); // eventDto에 있는 내용을 Event 타입의 인스턴스로 만들어달라

        // 이벤트를 만들고 저장하기 전에 이벤트를 갱신하여 유료인지 무료인지 여부를 변경해주자
        // 사실 이 소스는 서비스 쪽으로 위임해도 된다
        event.update();

        Event newEvent = eventRepository.save(event);

        // HATEOAS에서 제공
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash("{id}");
        URI createdUri = selfLinkBuilder.toUri();

        // Body에 이벤트를 담아서 리턴
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        // 이벤트 리소스에 넣었음 eventResource.add(selfLinkBuilder.withSelfRel());
        // PUT 요청을 하기 때문에 self와 링크가 같아도 상관없다.
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors)); // ErrorResource로 바꿔서 반환
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        // PagedResourcesAssembler를 사용하면 Page를 링크가 추가된 리소스로 변경할 수 있게된다.
        // 각 이벤트로 가는 링크를 제공하기 위해 이벤트를 이벤트 리소스로 변경하자
        PagedResources<Resource<Event>> pagedResources = assembler.toResource(page, e -> new EventResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        // mapping error
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // validation error
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();

        if (!eventRepository.existsById(id)) {
            //TODO create Errors
            return badRequest(errors);
        } else {
            Event updatedEvent = eventRepository.save(event);

            ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash("{id}");
            URI updatedUri = selfLinkBuilder.toUri();

            EventResource eventResource = new EventResource(event);
            eventResource.add(linkTo(EventController.class).withRel("query-events"));
            eventResource.add(selfLinkBuilder.withRel("update-event"));
            eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
            return ResponseEntity.created(updatedUri).body(eventResource);
        }
    }
}
