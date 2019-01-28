# 스프링 REST Docs 소개
https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
> 스프링 MVC Test를 사용해서 REST API 문서의 일부분들을 생성해내는 유용한 기능을 제공해주는 라이브러리  
> 테스트를 실행하면서 테스트를 실행할때 사용한 요청과 응답과 헤더를 이용해서 문서의 조각을 만들 수 있음  
> 문서의 조각을 만드는 것을 snippets 라고 부름  
> 문서의 조각들을 모아서 HTML로 REST API Documentation을 완성할 수 있음  

### Asciidoctor 문법 사용
> 기본적으로 REST Docs는 Asciidoctor 라는 툴을 사용해서 plain text로 작성한 문서를  
> Asciidoctor이라는 특별한 문법을 사용해서 만든 snippet을 조합해서 HTML 문서로 만들어줌  

### 테스트를 실행할때 어떻게 snippet를 생성하는 가?
1. MockMvc
2. WebTestClient
3. Rest Assured
4. Advanced(Slate, TestNG, JUnit5)

## REST Docs 자동 설정
- @AutoConfigureRestDocs

## REST Docs 코딩
- andDo(document(“doc-name”, snippets))
  > doc-name으로 문서를 생성하고 snippets들의 정보들을 추가해줌  
- snippets
  - links​()
  - requestParameters() + parameterWithName()
  - pathParameters() + parametersWithName()
  - requestParts() + partWithname()
  - requestPartBody()
  - requestPartFields()
  - requestHeaders() + headerWithName()
  - requestFields​() + fieldWithPath()
  - responseHeaders() + headerWithName()
  - responseFields​() + fieldWithPath()
  - ...
- Relaxed*
- Processor
  - preprocessRequest(prettyPrint()) ○ preprocessResponse(prettyPrint())
  - ...
## Constraint
https://github.com/spring-projects/spring-restdocs/blob/master/samples/rest-notes-spring-hateoas/src/test/java/com/example/notes/ApiDocumentation.java
## REST Docs 권장하는 이유
1. API 코드를 변경했을 때 테스트 코드도 성공하기 위해서 바꾸게 되면 문서도 자동으로 바뀜
2. 테스트를 하지 않은 내용이 추가로 생기면 그것 역시 문서화를 하도록 강제화 할 수 있음