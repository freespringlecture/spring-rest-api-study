# 스프링 HATEOAS 적용
> @EnableEntityLinks, @EnableHypermediaSupport 와 같은 애노테이션들을 사용해야지  
> HATEOAS를 사용할 수 있는데 스프링 부트가 HATEOAS를 자동으로 다 설정을 해주므로 그냥 사용할 수 있음  

## EvnetResource 만들기
> 스프링 HATEOAS에서 링크정보를 지원해주는 기능  
> 이벤트를 이벤트리소스로 변환하여 리소스를 만들어서 밖으로 내보내줌  
> 빈이 아니므로 매번 새롭게 컨버팅 해서 사용해야됨  
> JSON Arrays는 unwrapped가 안되는 문제가 있음  
- extends ResourceSupport의 문제
  - @JsonUnwrapped로 해결: 
    > beanSerializer이 json으로 만들때 자동으로 객체 변수를 참조하여 만드므로 event로 감싸지는데 wrapping을 unwrapping 하여 꺼내줌  
  - extends Resource<T>로 해결:
    > 이미 @JsonUnwrapped 가 적용되어 있어 코드량을 줄일 수 있음  
  
> Content type이 `application/hal+json` 이면 클라이언트들이 _links 필드에 링크정보를 들고있겠구나 예상할 수 있음  
> 링크정보를 바탕으로 링크를 Parsing 할 수 있음  

## 셀프링크 자동설정
> 보통 셀프링크의 경우 EventResource 마다 매번 설정을 해줘야 하므로 EventResource에 추가  

## 테스트 할 것
- 응답에 HATEOA와 profile 관련 링크가 있는지 확인
  - self (view)
  - update (만든 사람은 수정할 수 있으니까)
  - events (목록으로 가는 링크)