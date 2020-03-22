# 테스트 코드 리팩토링
## 여러 컨트롤러 간의 중복 코드 제거하기
- 클래스 상속을 사용하는 방법
- `@Ignore` 애노테이션으로 테스트로 간주되지 않도록 설정

## BaseControllerTest 라는 리팩토리용 설정 클래스 생성
공용으로 사용하는 중복 애노테이션과 의존성 주입 코드를 한군데 모아서 중복을 제거  
사용하는 곳에서는 `extends` 로 상속을 받아서 사용하면 됨  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class BaseControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ModelMapper modelMapper;
}
```

## EventControllerTests 가 BaseControllerTest를 상속받도록 수정
**BaseControllerTest** 를 상속받도록 수정하고 **BaseControllerTest** 에 이미 설정된 어노테이션과 `@Autowired` 객체 제거

```java
// 어노테이션 제거
// @RunWith(SpringRunner.class)
// @SpringBootTest
// @AutoConfigureMockMvc
// @AutoConfigureRestDocs
// @Import(RestDocsConfiguration.class)
// @ActiveProfiles("test")
public class EventControllerTests extends BaseControllerTest {

  // @Autowired 객체 제거
  // MockMvc mockMvc;

  // @Autowired 객체 제거
  // ObjectMapper objectMapper;

  // @Autowired 객체 제거
  // ModelMapper modelMapper;
```

## IndexControllerTest 도 BaseControllerTest를 상속받도록 수정
**BaseControllerTest** 를 상속받도록 수정하고 **BaseControllerTest** 에 이미 설정된 어노테이션과 `@Autowired` 객체 제거
```java
// 어노테이션 제거
// @RunWith(SpringRunner.class)
// @SpringBootTest
// @AutoConfigureMockMvc
// @AutoConfigureRestDocs
// @Import(RestDocsConfiguration.class)
// @ActiveProfiles("test")
public class IndexControllerTest extends BaseControllerTest {

  // @Autowired 객체 제거
  // MockMvc mockMvc;
```