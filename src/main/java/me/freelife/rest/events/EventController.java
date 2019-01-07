package me.freelife.rest.events;

import me.freelife.rest.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    /**
     * Event Create API
     * @param eventDto
     * @param errors
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        //EventDto에 있는 것을 Event 타입의 인스턴스로 만들어 달라
        Event event = modelMapper.map(eventDto, Event.class);
        //저장하기 전에 유료인지 무료인지 여부 업데이트
        event.update();
        Event newEvent = this.eventRepository.save(event);
        //EventController의 id에 해당하는 링크를 만들고 링크를 URI로 변환
        //API에 events에 어떤 특정한 ID 그 ID가 생성된 이벤트에 Location Header에 들어감
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event); //이벤트를 이벤트리소스로 변환
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event")); // 셀프 링크와 메서드는 같지만 사용하는 메서드만 다름
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        // createdUri 헤더를 가지고 201응답을 만듬
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    /**
     * Event List Select API
     * @param pageable
     * @param assembler
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResources = assembler.toResource(page, e -> new EventResource(e));
        //profile로 가는 Link를 추가
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
