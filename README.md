# 스프링 REST Docs 적용
## REST Docs 자동 설정
> 테스트에 설정하면 target/generated-snippets 디렉토리를 생성하고 snippets 정보를 추가해줌  
> 매번 테스트를 실행할때마다 snippets 들은 오버라이딩 되서 덮어씌워짐  
#### @AutoConfigureRestDocs
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {
```

## RestDocMockMvc 커스터마이징
> RestDocs 요청 응답 포멧팅 하기위해 커스터마이징이 필요  
- RestDocsMockMvcConfigurationCustomizer 구현한 빈 등록
#### @TestConfiguration
```java
@TestConfiguration
public class RestDocsConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return new RestDocsMockMvcConfigurationCustomizer() {
            @Override
            public void customize(MockMvcRestDocumentationConfigurer configurer) {
                configurer.operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint());
            }
        };
    }

}
```
## 테스트 할 것
- API 문서 만들기
  - 요청 본문 문서화
  - 응답 본문 문서화
  - 링크문서화
    - profile 링크 추가
  - 응답 헤더 문서화