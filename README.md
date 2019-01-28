# Event 목록 조회 API
 
## 페이징, 정렬 어떻게 하지?
- 스프링 데이터 JPA가 제공하는 Pageable
 
## Page<Event>에 안에 들어있는 Event 들은 리소스로 어떻게 변경할까?
- 하나씩 순회하면서 직접 EventResource로 맵핑을 시킬까..
- PagedResourceAssembler<T> 사용하기
 
## 테스트 할 때 Pageable 파라미터 제공하는 방법
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web
- page: 0부터 시작
- size: 기본값 20
- sort: property,property(,ASC|DESC)
 
## 테스트 할 것
- Event 목록 Page 정보와 함께 받기
  - content[0].id 확인
  - pageable 경로 확인
- Sort과 Paging 확인
  - 30개를 만들고, 10개 사이즈로 두번째 페이지 조회하면 이전, 다음 페이지로 가는 링크가 있어야 한다.
  - 이벤트 이름순으로 정렬하기
  - page 관련 링크
- Event를 EventResource로 변환해서 받기
  - 각 이벤트 마다 self
  ```java
  var pagedResources = assembler.toResource(page, e -> new EventResource(e));
  ```
- 링크 확인
  - self
  - profile
  - (create)
- 문서화
