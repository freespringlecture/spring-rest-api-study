# Event 생성 API 구현: Bad Request 응답 본문 만들기
> 응답 본문에 메세지가 있기를 바라고 메세지를 만드려면 어떻게 해야되는지  
> 응답 정보들은 Errors 객체에 들어있음  
> 어떤 필드에 어떤 값 때문에 왜 에러가 났는지 보여주고 클라이언트가 적절히 판단하도록 응답에 실어서 보내준다  

## Errors JSON 변환의 문제점
> BeanSerializer을 사용해서 Java Bean Spec을 준수하는 객체를 JSON으로 변환할 수 있는데  
> ObjectMapper에 여러가지 Serializer가 등록이 되어있는데  
> Errors는 Java Bean Spec을 준수하는 객체가 아니라 JSON객체로 변환할 수가 없음  
> `produces = MediaTypes.HAL_JSON_UTF8_VALUE` 이 지정되어있어 응답 처리시 JSON으로 변환하는 처리를 하는데  
> 변환 처리를 할 수 없어서 에러가 발생함  

## 문제 해결을 위해 커스텀 JSON Serializer 만들기
- extends JsonSerializer<T> (Jackson JSON 제공)
- @JsonComponent: (스프링 부트 제공)매우 쉽게 등록할 수 있음

### ErrorsSerializer 만들기
> 필드에러랑 글로벌 에러를 둘다 맵핑해서 JSON에 담아줘야함  
> ObjectMapper는 ErrorSerializer를 사용함 Errors라는 객체를 Serialization 할 때  

## Errors
- rejectValue: 필드 에러
- reject: 글로벌 에러

## BindingError
- FieldError 와 GlobalError (ObjectError)가 있음
- objectName
- defaultMessage
- code
- field
- rejectedValue

## 테스트 할 것
- 입력 데이터가 이상한 경우 Bad_Request로 응답
  - 입력값이 이상한 경우 에러
  - 비즈니스 로직으로 검사할 수 있는 에러
  - 에러 응답 메시지에 에러에 대한 정보가 있어야 한다