# Event 생성 API 구현: 매개변수를 이용한 테스트
## 테스트 코드 리팩토링
- 테스트에서 중복 코드 제거
- 매개변수만 바꿀 수 있으면 좋겠는데?
- JUnitParams

## JUnitParams
https://github.com/Pragmatists/JUnitParams
> 파라메터를 사용한 테스트 코드를 만들기 쉽게 해주는 라이브러리  
> 원래 JUnit은 메서드 파라메터를 가질 수 없음  
> 메서드 파라메터에 들어갈 값들을 정의하는 방법을 제공  
```xml
<dependency>
    <groupId>pl.pragmatists</groupId>
    <artifactId>JUnitParams</artifactId>
    <version>1.1.1</version>
    <scope>test</scope>
</dependency>
```

#### @RunWith 설정
```java
@RunWith(JUnitParamsRunner.class)
```