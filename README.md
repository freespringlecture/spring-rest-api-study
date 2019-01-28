# Event 생성 API 구현: EventRepository 구현
## 스프링 데이터 JPA
- JpaRepository 상속 받아 만들기

## Enum을 JPA 맵핑시 주의할 것
- @Enumerated(EnumType.STRING)

## @MockBean
- Mockito를 사용해서 mock 객체를 만들고 빈으로 등록해 줌
- (주의) 기존 빈을 테스트용 빈이 대체 한다
## 테스트 할 것
- 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
  - Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인
  - id는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인