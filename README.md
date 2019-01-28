# 스프링 HATEOAS 소개
## 스프링 HATEOAS
https://docs.spring.io/spring-hateoas/docs/current/reference/html/
### 링크 만드는 기능
- 문자열 가지고 만들기
- 컨트롤러와 메소드로 만들기
### 리소스 만드는 기능
- 리소스: 데이터 + 링크
### 링크 찾아주는 기능
> 사용자 입장에서 링크를 가져와서 쉽게 사용할 수 있는 기능  
- Traverson
- LinkDiscoverers
### 링크
> 링크 자체에 어떠한 메서드를 사용해야 한다를 현재 설정할 수는 없음  
- HREF(Hyper Media Reference): URI나 URL 설정
- REL(Relation): 현재 이 소스와의 관계
  - self: 자기자신의 URL
  - profile: 현재 이 응답본문에 대한 설명을 가지고 있는 문서로 링크
  - update-event: 이벤트를 수정할 수 있는 업데이트
  - query-events: 이벤트를 조회할 수 있는 링크
  
<img src="img/3-1.png" width="300">
  