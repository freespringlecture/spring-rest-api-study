# 테스트 코드 리팩토링
### 여러 컨트롤러 간의 중복 코드 제거하기
- 클래스 상속을 사용하는 방법
- @Ignore 애노테이션으로 테스트로 간주되지 않도록 설정

#### BaseControllerTest 라는 리팩토리용 설정 클래스 생성
> 공용으로 사용하는 중복 애노테이션과 의존성 주입 코드를 한군데 모아서 중복을 제거  
> 사용하는 곳에서는 extends 로 상속을 받아서 사용하면 됨  
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