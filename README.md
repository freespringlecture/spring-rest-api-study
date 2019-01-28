# Event 생성 API 구현: 입력값 이외에 에러 발생
> Event를 사용해서 이상한 값들을 넣어주면 응답이 400(Bad Request)로 나오길 바람
## ObjectMapper 커스터마이징
> 스프링 부트가 제공한 properties를 사용한 ObjectMapper 확장기능을 사용  
- deserialization: JSON 문자열을 Object로 변환하는 과정
- serialization: Object를 JSON으로 변환하는 과정

#### deserialization 할때 unKnown properties가 있으면 실패하라는 옵션
```
spring.jackson.deserialization.fail-on-unknown-properties=true
```

## 동작 원리
> id, free, offline, eventStatus와 같이 unKnown properties를 넘기면 에러가 발생함  
> 에러가 발생하면 스프링MVC는 기본적으로 400(Bad Request)로 처리함  

## 테스트 할 것
- 입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면?
  - **Bad_Request로 응답**​​ vs 받기로 한 값 이외는 무시