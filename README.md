# 깨진 테스트 살펴보기
## `EventControllerTests.updateEvent()`
### 깨지는 이유 및 기존 소스코드
기존 **Manager**가 있고 **AccessToken**이 들어온 경우에는 링크가 추가로 보이고 아니면 안보이게 했어야 하는데 그렇게 하지 않아서 오류가 발생
**generateEvent**의 **Manager**가 `null` 이라서 `NullPointerException`이 발생하는 상태
```java

...

private String getBearerToken() throws Exception {
    return "Bearer " + getAccessToken();
}

 /**
  * 인증 토큰을 발급
  * @return
  * @throws Exception
  */
private String getAccessToken() throws Exception {
    // Given
    Account freelife = Account.builder()
            .email(appProperties.getUserUsername())
            .password(appProperties.getUserPassword())
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    this.accountService.saveAccount(freelife);

    ...

}

...

@Test
@TestDescription("이벤트를 정상적으로 수정하기")
public void updateEvent() throws Exception {
    // Given
    Event event = this.generateEvent(200);

    ...

}

...

private Event generateEvent(int index) {
    Event event = Event.builder()
            .name("event " + index)
            .description("test event")
            .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
            .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
            .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 D2 스타텁 팩토리")
            .free(false)
            .offline(true)
            .eventStatus(EventStatus.DRAFT)
            .build();

    return this.eventRepository.save(event);
}
```
### 해결방법
`updateEvent()` 수행시 **Account**를 신규로 생성해서 **Event** 생성 메서드에 **account** 파라메터를 넘겨 
**Manager**를 셋팅해서 저장하도록 수정

- `getAcessToken`에 포함되어있던 **Account** 생성 기능을 `createAccount()`로 리팩토링
- `getBearerToken`을 오버로딩하여 `boolean needToCreateAccount`을 파라메터로 받는 메서드와 파라메터를 받지 않으면
  `true`를 기본값으로 대입하도록 수정
- `getAccessToken` 메서드는 `needToCreateAccount` 파라메터에 따라 `createAccount()`를 수행하여 회원정보를 생성하도록 수정
- `updeateEvent` 메서드를 **createAccount()**를 수행하여 생성한 **account**를 `generateEvent`에 추가로 파라메터 전달
- `generateEvent` 메서드가 **account** 파라메터를 추가로 받으면 **Manager**에 파라메터로 받은 **account**를 셋팅해서 저장
  **index** 파라메터만 받는다면 기존과 동일하게 **event** 객체를 저장
- `generateEvent`에 포함되어있던 **Event Builder** 를 `buildEvent`로 리팩토링
```java
// 오버로딩하여 파라메터를 받지 않으면 getAccessToken에 true를 기본 파라메터로 전달
private String getBearerToken() throws Exception {
    return getBearerToken(true);
}

// needToCreateAccount 파라메터를 받아서 getAccessToken에 파라메터를 전달
private String getBearerToken(boolean needToCreateAccount) throws Exception {
    return "Bearer " + getAccessToken(needToCreateAccount);
}

 /**
  * 인증 토큰을 발급
  needToCreateAccount 파라메터에 따라 createAccount()를 수행하여 회원정보를 생성하도록 수정
  * @return
  * @throws Exception
  */
private String getAccessToken(boolean needToCreateAccount) throws Exception {
    // Given
    if(needToCreateAccount)
        createAccount();

    ...

}

// getAcessToken에 포함되어있던 Account 생성 기능을 createAccount()로 리팩토링
private Account createAccount() {
    Account freelife = Account.builder()
            .email(appProperties.getUserUsername())
            .password(appProperties.getUserPassword())
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    return this.accountService.saveAccount(freelife);
}

// Account를 createAccount()를 수행하여 생성하고 generateEvent에 account 파라메터를 추가로 전달
@Test
@TestDescription("이벤트를 정상적으로 수정하기")
public void updateEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(200, account);

    ...

}

// account 파라메터를 추가로 받으면 Manager에 파라메터로 받은 account를 셋팅해서 저장
private Event generateEvent(int index, Account account) {
    Event event = buildEvent(index);
    event.setManager(account);
    return this.eventRepository.save(event);
}

// index만 있으면 기존과 동일하게 event 객체를 저장
private Event generateEvent(int index) {
    Event event = buildEvent(index);
    return this.eventRepository.save(event);
}

// generateEvent에 포함되어있던 Event Builder 를 buildEvent로 리팩토링
private Event buildEvent(int index) {
    return Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
}
```

## `EventControllerTests.getEvent()`
### 깨지는이유 및 소스코드
마찬가지로 `getEvent()` 테스트 코드도 **generateEvent**의 **Manager**가 `null` 이라서 `NullPointerException`이 발생하는 상태

```java
@Test
@TestDescription("기존의 이벤트를 하나 조회하기")
public void getEvent() throws Exception {
    // Given
    Event event = this.generateEvent(100);

    ...

}
```
### 해결방법 및 소스코드
**createAccount()**를 수행하여 생성한 **account**를 `generateEvent`에 추가로 파라메터 전달

```java
// Account를 createAccount()를 수행하여 생성하고 generateEvent에 account 파라메터를 추가로 전달
@Test
@TestDescription("기존의 이벤트를 하나 조회하기")
public void getEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(100, account);

    ...

}
```

## `DemoApplicationTests`
### 깨지는이유 및 소스코드
이 테스트 코드는 `application-test.properties` 파일에서 설정을 읽어오지 않고 `application.properties` 파일에서 설정을 읽어오기 떄문에 `h2` **DB**를 사용하지 않고 현재 띄워놓지 않은 `PostgreSQL` 커넥션을 시도하면서 테스트가 실패함

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
```
### 해결방법 및 소스코드
`@ActiveProfiles("test")` 어노테이션을 추가해서 `application-test.properties` 파일에서 설정을 읽어 오도록 수정하면 테스트가 성공한다
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
```