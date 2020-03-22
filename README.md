# 스프링 시큐리티 OAuth 2 설정: 리소스 서버 설정
- 설정한 **OAuth2** 서버와 연동이 되어서 사용이 됨  
- 어떤 외부 요청이 리소스에 접근할 때   
- 클라이언트가 인증이 필요하다면 **OAuth2** 서버에서 제공하는 토큰 서비스에 요청을 보내서  
- 토큰을 발급받고 토큰 기반으로 인증정보가 있는지 없는지 확인하고 리소스 서버에 접근을 제한함  
- 리소스 서버는 이벤트 리소스를 제공하는 서버와 같이 있는게 맞고 인증서버는 따로 분리하는게 맞음  
- 작은 서비스에서는 같이 사용해도 상관은 없음 
 
## EventControllerTests 테스트 수정
`GET`을 제외하고 모두 엑세스 토큰을 가지고 요청 하도록 테스트 수정

### header에 토큰을 넣어주도록 수정
```java
.header(HttpHeaders.AUTHORIZATION, getBearerToken())

private String getBearerToken() throws Exception {
    return "Bearer " + getAccessToken();
}
```

### 인증토큰 발급 메서드 추가
```java
    /**
    * 인증 토큰을 발급
    * @return
    * @throws Exception
    */
private String getAccessToken() throws Exception {
    // Given
    String username = "freelife@gmail.com";
    String password = "freelife";
    Account freelife = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    this.accountService.saveAccount(freelife);

    String clientId = "myApp";
    String clientSecret = "pass";
    ResultActions perform = this.mockMvc.perform(post("/oauth/token")
            .with(httpBasic(clientId, clientSecret)) // Basic OAuth Header
            .param("username", username)
            .param("password", password)
            .param("grant_type", "password"));
    var responseBody = perform.andReturn().getResponse().getContentAsString();
    Jackson2JsonParser parser = new Jackson2JsonParser();
    return parser.parseMap(responseBody).get("access_token").toString();
}
```
 
## ResourceServer 설정
- `@EnableResourceServer`
- `extends ResourceServerConfigurerAdapter`
- `configure(ResourceServerSecurityConfigurer resources)`
  - 리소스 ID
- `configure(HttpSecurity http)`
  - **anonymous**
  - `GET /api/**` : **permit all**
  - `POST /api/**`: **authenticated**
  - `PUT /api/**`: **authenticated**
  - 에러 처리
    - `accessDeniedHandler(OAuth2AccessDeniedHandler())`

### ResourceServiceConfig 클래스 추가
```java
package me.freelife.rest.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                    .anonymous()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling() // 인증이 잘못됐다던가 권한이 없는 경우 발생하는 예외중에 접근권한이 없는 것은
                .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // OAuth2AccessDeniedHandler 를 사용함 403으로 status 응답을 내보내줌
    }
}
```

### EventControllerTests 에 인증 토큰 발급 코드를 추가하고 테스트코드 전체적으로 수정

아래의 인증 토큰 발급 코드를 추가하고 모든 테스트 코드에 `.header(HttpHeaders.AUTHORIZATION, getBearerToken())` 코드 추가

```java

...

import me.freelife.rest.accounts.Account;
import me.freelife.rest.accounts.AccountRepository;
import me.freelife.rest.accounts.AccountRole;
import me.freelife.rest.accounts.AccountService;
import org.junit.Before;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

public class EventControllerTests extends BaseControllerTest {

    ...

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

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
        String username = "freelife@gmail.com";
        String password = "freelife";
        Account freelife = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(freelife);

        String clientId = "myApp";
        String clientSecret = "pass";
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret)) // Basic OAuth Header
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"));
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    ...

```

## `javax.persistence.NonUniqueResultException` 에러발생
- **uniq** 해야되는데 계속 동일한 유저를 저장하니까 에러가남  
- 아이디를 랜덤으로 생성하거나 테스트 하기전에 데이터를 비워줌  
- **InMemoryDB** 이지만 테스트 진행중에는 **DB**를 공유하므로 데이터가 공유되므로  
- 데이터가 독립적이지 않으므로 **setUp**이나 **after**에서 데이터를 다 지움  
- 테스트시 한 건씩은 괜찮지만 여러건을 한번에 테스트시에는 주의해야함  

### 테스트시 DB를 비우면서 테스트
```java
@Autowired
AccountRepository accountRepository;

@Before
public void setUp() {
    this.eventRepository.deleteAll();
    this.accountRepository.deleteAll();
}
```