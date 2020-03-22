# Events 수정 API 구현
## EventController에 updateEvent 메서드 추가
```java
@PutMapping("/{id}")
public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
  Optional<Event> optionalEvent = this.eventRepository.findById(id);
  if(optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
  }

  if(errors.hasErrors()) {
      return badRequest(errors);
  }

  this.eventValidator.validate(eventDto, errors);
  if(errors.hasErrors()) {
      return badRequest(errors);
  }

  Event existingEvent = optionalEvent.get();
  this.modelMapper.map(eventDto, existingEvent);
  Event saveEvent = this.eventRepository.save(existingEvent);

  EventResource eventResource = new EventResource(saveEvent);
  eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

  return ResponseEntity.ok(eventResource);
}
```

## EventControllerTests에 updateEvent 테스트 코드 추가
```java
@Autowired
ModelMapper modelMapper;

@Test
@TestDescription("이벤트를 정상적으로 수정하기")
public void updateEvent() throws Exception {
  // Given
  Event event = this.generateEvent(200);
  EventDto eventDto = this.modelMapper.map(event, EventDto.class);
  String eventName = "Updated Event";
  eventDto.setName(eventName);

  // When & Then
  this.mockMvc.perform(put("/api/events/{id}", event.getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(this.objectMapper.writeValueAsString(eventDto)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("name").value(eventName))
          .andExpect(jsonPath("_links.self").exists())
          .andDo(document("update-event"))
  ;
}

@Test
@TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
public void updateEvent400_Empty() throws Exception {
  // Given
  Event event = this.generateEvent(200);

  EventDto eventDto = new EventDto();

  // When & Then
  this.mockMvc.perform(put("/api/events/{id}", event.getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(this.objectMapper.writeValueAsString(eventDto)))
          .andDo(print())
          .andExpect(status().isBadRequest());
}

@Test
@TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
public void updateEvent400_Wrong() throws Exception {
  // Given
  Event event = this.generateEvent(200);

  EventDto eventDto = this.modelMapper.map(event, EventDto.class);
  eventDto.setBasePrice(20000);
  eventDto.setMaxPrice(1000);

  // When & Then
  this.mockMvc.perform(put("/api/events/{id}", event.getId())
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(this.objectMapper.writeValueAsString(eventDto)))
          .andDo(print())
          .andExpect(status().isBadRequest());
}

@Test
@TestDescription("존재하지 않는 이벤트 수정 실패")
public void updateEvent404() throws Exception {
  // Given
  Event event = this.generateEvent(200);
  EventDto eventDto = this.modelMapper.map(event, EventDto.class);

  // When & Then
  this.mockMvc.perform(put("/api/events/123123")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(this.objectMapper.writeValueAsString(eventDto)))
          .andDo(print())
          .andExpect(status().isNotFound());
}

// generateEvent builder에 셋팅 값 추가
private Event generateEvent(int index) {
  Event event = Event.builder()
          .name("event " + index)
          .description("test event")
          .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
          .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
          .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
          .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
          .basePrice(100)
          .maxPrice(200)
          .limitOfEnrollment(100)
          .location("강남역 D2 스타텁 팩토리")
          .free(false)
          .offline(true)
          .eventStatus(EventStatus.DRAFT)
          .build();

  return this.eventRepository.save(event);
}

```

## 테스트 할 것
- 수정하려는 이벤트가 없는 경우 404 **NOT_FOUND**  
- 입력 데이터 (데이터 바인딩)가 이상한 경우에 400 **BAD_REQUEST**  
- 도메인 로직으로 데이터 검증 실패하면 400 **BAD_REQUEST**  
- (권한이 충분하지 않은 경우에 403 **FORBIDDEN**)  

### 정상적으로 수정한 경우에 이벤트 리소스 응답  
- 200 OK
- 링크
- 수정한 이벤트 데이터