# Event 생성 API 구현: 입력값 제한하기
## 입력값 제한
### 입력하기로한 값들 이외에는 무시하는 방법  
- id 또는 입력받은 데이터로 계산 해야 하는 값들은 입력을 받지 않아야 한다
- EventDto 적용
  > 너무 많은 애노테이션으로 코드가 복잡하고 지저분해지므로 분리해서 작업  
  > 입력받는 DTO 별도로 복사해서 처리  
  > 받아올 객체가 EventDto이기 떄문에 id가 있던 free가 있던 무시  
## DTO -> 도메인 객체로 값 복사
> 기존 클래스를 DTO로 손쉽게 변환해주는 라이브러리  
#### ModelMapper
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.3.2</version>
</dependency>
```

#### ModelMapper 빈 등록
```java
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}
```
  
> 입력값은 EventDto로 받았지만 계산되어야 되는 필드들은 없으므로 걸러서 받고  
> 걸러진 값들을 대상으로 이벤트 객체를 생성해서 eventRepository에 저장을 함  
  

## 통합 테스트로 전환
> Mocking 테스트를 하려고 만든 객체와 새롭게 EventDto로 생성한 객체가 달라서 null을 리턴해줘서 NullPointException이 일어남  
- @WebMvcTest 빼고 다음 애노테이션 추가
  - @SpringBootTest
  > 테스트할때는 @SpringBootTest로 테스트하는게 편함 Mocking 해줘야 될게 너무많아서 관리가 힘듬  
  > 애플리케이션을 실행했을때와 가장 근사한 테스트를 만들어 작성할 수 있음  
  - @AutoConfigureMockMvc
  > MockMvc를 계속 사용하기 위해 적용  
- Repository @MockBean 코드 제거

## 테스트 할 것
- 입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면?
  - Bad_Request로 응답 vs ​**받기로 한 값 이외는 무시**