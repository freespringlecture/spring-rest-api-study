package me.freelife.rest.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * 스프링 HATEOAS를 사용해 이벤트를 이벤트리소스로 변환하여 리소스를 만들어서 밖으로 내보내줌
 */
public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        // add(new Link("http://localhost:8080/api/events" + event.getId()));
        // 셀프 링크 생성 위와 동일한 링크
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
