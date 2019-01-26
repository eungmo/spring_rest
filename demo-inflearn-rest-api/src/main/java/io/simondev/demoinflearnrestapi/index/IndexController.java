package io.simondev.demoinflearnrestapi.index;

import io.simondev.demoinflearnrestapi.events.EventController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public ResourceSupport index() {
        var index = new ResourceSupport(); // 리소스 링크 정보를 리턴
        index.add(linkTo(EventController.class).withRel("events")); // HTML로 따지면 햄버거 메뉴를 추가하는 것과 비슷한 것 같다.

        return index;
    }
}
