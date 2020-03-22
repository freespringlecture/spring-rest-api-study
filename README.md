# Event 목록 조회 API 구현
 
## 페이징, 정렬 어떻게 하지?
- 스프링 데이터 **JPA**가 제공하는 **Pageable**
 
## `Page<Event>`에 안에 들어있는 Event 들은 리소스로 어떻게 변경할까?
- 하나씩 순회하면서 직접 **EventResource**로 맵핑을 시킬까..
- `PagedResourceAssembler<T>` 사용하기

### EventController에 queryEvents 메서드 추가
`PagedResourceAssembler<T>` 를 사용하는 **queryEvents** 메서드 추가
```java

...

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.bind.annotation.GetMapping;

/**
  * Event Create API
  * @param eventDto
  * @param errors
  * @return
  */
@PostMapping
public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    
    ...

    // eventResource.add(new Link("/docs/index.html").withRel("profile")); 를 아래와 같이 변경
    eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
    
    ...

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
```
 
## 테스트 할 때 Pageable 파라미터 제공하는 방법
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web

- **page**: 0부터 시작
- **size**: 기본값 20
- **sort**: **property**, **property**(**ASC**|**DESC**)

### EventControllerTests 에 queryEvents 테스트 코드 추가
```java
@Autowired
EventRepository eventRepository;


...

@Test
@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
public void queryEvents() throws Exception {
    //Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When
    this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("query-events"))
    ;
}

private void generateEvent(int index) {
    Event event = Event.builder()
            .name("event " + index)
            .description("test event")
            .build();

    this.eventRepository.save(event);
}
```
 
## 테스트 할 것
- **Event** 목록 **Page** 정보와 함께 받기
  - `content[0].id` 확인
  - **pageable** 경로 확인
- **Sort**과 **Paging** 확인
  - 30개를 만들고, 10개 사이즈로 두번째 페이지 조회하면 이전, 다음 페이지로 가는 링크가 있어야 한다.
  - 이벤트 이름순으로 정렬하기
  - **page** 관련 링크
- **Event**를 **EventResource**로 변환해서 받기
  - 각 이벤트 마다 **self**
  ```java
  var pagedResources = assembler.toResource(page, e -> new EventResource(e));
  ```
- 링크 확인
  - **self**
  - **profile**
  - (**create**)
- 문서화