# Event 생성 API 구현: 201 응답 받기
## @RestController
> @ResponseBody를 모든 메소드에 적용한 것과 동일하다  
## ResponseEntity를 사용하는 이유
> 응답코드,헤더,본문모두다루기편한API  
## Location URI 만들기
> HATEOS가 제공하는 linkTo(), methodOn() 사용  
> methodOn()에서 메서드를 호출할 때 메서드에 있는 URL을 읽어와야 되는데 어떤메서드에 있는걸 읽어와야될지 메서드를 호출해서 지정함(타입세이프)  
> 파라메터를 추가하는 바람에 null을 createEvent()에 추가함 @RequestMapping로 @Controller에 있는 모든 Handler에 적용되도록 설정  
## 객체를 JSON으로 변환
> ObjectMapper 사용  
## 테스트 할 것
- 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
  - Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인
  - id는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인