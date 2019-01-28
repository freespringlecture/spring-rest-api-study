# Event 생성 API 구현: Bad Request 처리하기
> 입력값이 이상한 경우에 Bad Request를 보내는 방법
## @Valid와 BindingResult (또는 Errors)
> 스프링MVC에 해당하는 내용 JS303 애노테이션을 사용해 확인할 수 있음  
> @Valid 라는 애노테이션을 붙이면 Entity에 바인딩을 할때 애노테이션들에 대한 정보를 참고해서 검증을 수행함  
> 검증을 수행한 결과를 객체 오른쪽에 있는 Errors 객체에 에러값들을 넣어줌  
> 받은 에러를 확인 해서 Bad Request를 발생시킴  
  
- BindingResult는 항상 @Valid 바로 다음 인자로 사용해야 함 (스프링 MVC)
- @NotNull, @NotEmpty, @Min, @Max, ... 사용해서 입력값 바인딩할 때 에러 확인할 수 있음
  
## 도메인 Validator 만들기
- [Validator](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/validation/Validator.html)​ 인터페이스 없이 만들어도 상관없음  
> Junit5로 테스트하면 테스트 설명이 나옴  

## 테스트 설명 용 애노테이션 만들기
- @Target, @Retention

## 테스트 할 것
- 입력 데이터가 이상한 경우 Bad_Request로 응답
  - _입력값이 이상한 경우 에러_
  - _비즈니스 로직으로 검사할 수 있는 에러_
  - 에러 응답 메시지에 에러에 대한 정보가 있어야 한다