# 예외 테스트
  
## 1. `@Test(expected)`
예외 타입만 확인 가능  
```java
@Test(expected = UsernameNotFoundException.class)
@TestDescription("없는 사용자의 경우 UsernameNotFoundException 예외 발생")
public void findByUsernameFail() {
  String username = "random@email.com";
  accountService.loadUserByUsername(username);
}
```

## 2. try-catch
- 예외 타입과 메시지 확인 가능  
- 하지만 코드가 다소 복잡  
```java
@Test
@TestDescription("없는 사용자의 경우 예외 타입과 메세지 확인")
public void findByUsernameFail() {
  String username = "random@email.com";
  try {
      accountService.loadUserByUsername(username);
      fail("supposed to be failed");
  } catch (UsernameNotFoundException e) {
      assertThat(e.getMessage()).containsSequence(username);
  }
}
```
 
## 3. `@Rule` ExpectedException
- 코드는 간결하면서 예외 타입과 메시지 모두 확인 가능  
- Expected 라서 예상되는 예외를 먼저 작성해줘야 함  

### AccountServiceTest 에 `@Rule` 설정
```java

...

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.fail;

public class AccountServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  ...
  
```

### expectedException 객체를 사용해서 예외처리
```java
@Test
@TestDescription("없는 사용자의 경우 예상되는 예외로 타입과 메세지 확인")
public void findByUsernameFail() {
  // Expected
  String username = "random@email.com";
  expectedException.expect(UsernameNotFoundException.class);
  expectedException.expectMessage(Matchers.containsString(username));

  // When
  accountService.loadUserByUsername(username);
}
```