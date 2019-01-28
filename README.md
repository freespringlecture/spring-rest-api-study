# 인덱스 핸들러 만들기
> 웹 페이지를 처음 접근할 때 API의 진입점이 필요함  
> 진입점에 해당하는 index 만들기  

## 인덱스 핸들러
> badRequest를 만들때 errors를 받아서 본문에 넣어주는데  
> ErrorsResources 로 변환하고 Resource로 변환할때 index에 대한 링크를 추가하도록 설정  
- 다른리소스에대한링크제공
- 문서화
```java
@GetMapping("/api")
public ResourceSupport root() {
  ResourceSupport index = new ResourceSupport();
  index.add(linkTo(EventController.class).withRel("events"));
  return index;
}
```

## 테스트 컨트롤러 리팩토링
- 중복코드제거
## 에러 리소스
- 에러 발생 시 인덱스로 가는 링크 제공
```java
public class ErrorsResource extends Resource<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        add(linkTo(methodOn(IndexController.class)).withRel("index"));
    }
}
```

## ErrorsResource unwrapping 문제
> 스프링 HATEOAS 적용에서 처리했던 것과 비슷한 부분 @JsonUnwrapped로 했던 부분  
> 스프링 부트가 제공해주는 ResorceSupport 하위에 extends Resource<T>로 처리함  
> JSON 이라서 unwrapped가 되었는데 여기서는 content가 JSON Arrays여서 unwrapped가 안되고  
> content로 wrapping되므로 content[0]의 값을 검증하도록 테스트 코드 변경  
```java
.andExpect(jsonPath("content[0].objectName").exists())
.andExpect(jsonPath("content[0].defaultMessage").exists())
.andExpect(jsonPath("content[0].code").exists())
.andExpect(jsonPath("_links.index").exists())
```