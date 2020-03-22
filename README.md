# Event 조회 API 구현

## EventController 에 조회 이벤트 구현
```java
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
```

## EventControllerTests 에 조회 이벤트 테스트 코드 구현
```java

...

// queryEvents 테스트코드 ResultActions 에 담아서 처리하도록 변경
@Test
@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
public void queryEvents() throws Exception {
  //Given
  IntStream.range(0, 30).forEach(this::generateEvent);

  // When & Then
  ResultActions perform = this.mockMvc.perform(get("/api/events")
          .param("page", "1")
          .param("size", "10")
          .param("sort", "name,DESC")
  );

  // Then
  perform.andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("page").exists())
          .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
          .andExpect(jsonPath("_links.self").exists())
          .andExpect(jsonPath("_links.profile").exists())
          .andDo(document("query-events"))
  ;
}

...

@Test
@TestDescription("기존의 이벤트를 하나 조회하기")
public void getEvent() throws Exception {
  // Given
  Event event = this.generateEvent(100);

  // When & Then
  this.mockMvc.perform((get("/api/events/{id}", event.getId())))
          .andExpect(status().isOk())
          .andExpect(jsonPath("name").exists())
          .andExpect(jsonPath("id").exists())
          .andExpect(jsonPath("_links.self").exists())
          .andExpect(jsonPath("_links.profile").exists())
          .andDo(document("get-an-event"))
  ;
}

@Test
@TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
public void getEvent404() throws Exception {
  // When & Then
  this.mockMvc.perform(get("/api/events/11883"))
          .andExpect(status().isNotFound());
}

private Event generateEvent(int index) {
  Event event = Event.builder()
          .name("event " + index)
          .description("test event")
          .build();

  return this.eventRepository.save(event);
}
```

## 테스트 할 것
### 조회하는 이벤트가 있는 경우 이벤트 리소스 확인
- 링크
- **self**
- **profile**
- (**update**)
- 이벤트 데이터
 
### 조회하는 이벤트가 없는 경우 404 응답 확인 