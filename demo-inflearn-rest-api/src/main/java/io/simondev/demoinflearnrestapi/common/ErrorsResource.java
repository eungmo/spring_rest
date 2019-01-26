package io.simondev.demoinflearnrestapi.common;

import io.simondev.demoinflearnrestapi.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ErrorsResource extends Resource<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        // 링크를 추가한다. 인덱스 컨트롤러의 index()로 가는 링크를 추가
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
