# 문자열을 외부 설정으로 빼내기
## 유저 이메일 unique 설정
유저 저장시 겹치지 않도록 설정  

### Account email unique 설정
```java
@Column(unique = true)
private String email;
```

## Properties 자동완성 의존성 추가
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```
 
## 외부 설정으로 기본 유저와 클라이언트 정보 빼내기
### AppProperties 클래스 생성
외부 설정으로 기본 유저와 클라이언트 정보를 빼내기 위한 프로퍼티 설정  
```java
package me.freelife.rest.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {

    @NotEmpty
    private String adminUsername;

    @NotEmpty
    private String adminPassword;

    @NotEmpty
    private String userUsername;

    @NotEmpty
    private String userPassword;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;
}
```

### `application.properties` 에 프로퍼티 값 추가
```properties
my-app.admin-username=admin@email.com
my-app.admin-password=admin
my-app.user-username=user@email.com
my-app.user-password=user
my-app.client-id=myApp
my-app.client-secret=pass
```

## 기본 유저 만들기
- `ApplicationRunner`
  - **Admin**
  - **User**
  
### AppConfig 에 기본유저 admin, user 생성되도록 수정하고 프로퍼티 설정추가  
```java
//임의의 유저정보 생성
@Bean
public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {
        @Autowired
        AccountService accountService;

        @Autowired
        AppProperties appProperties;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            Account admin = Account.builder()
                    .email(appProperties.getAdminUsername())
                    .password(appProperties.getAdminPassword())
                    .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                    .build();
            accountService.saveAccount(admin);

            Account user = Account.builder()
                    .email(appProperties.getUserUsername())
                    .password(appProperties.getUserPassword())
                    .roles(Set.of(AccountRole.USER))
                    .build();
            accountService.saveAccount(user);
        }
    };
}
```

### AuthServerConfig에 프로퍼티 설정 추가 
**OAuth2** 서버 설정 부분에 프로퍼티 설정 추가
```java
@Autowired
AppProperties appProperties;

@Override
public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory() // inMemory 용으로 생성 원래는 jdbc로 DB로 생성해야됨
            .withClient(appProperties.getClientId()) // myApp에 대한 클라이언트를 하나 생성
            .authorizedGrantTypes("password", "refresh_token") // 지원하는 grant_Type
            .scopes("read", "write") // 임의 값
            .secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
            .accessTokenValiditySeconds(10 * 60) // 엑세스 토큰의 유효시간 10분
            .refreshTokenValiditySeconds(6 * 10 * 60); // refresh_token의 유효시간
}
```

## 테스트 코드에 프로퍼티 설정 추가
### AuthServerConfigTest 에 프로퍼티 설정 추가
```java

...

import me.freelife.rest.common.AppProperties;

public class AuthServerConfigTest extends BaseControllerTest {

    ...

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // Basic OAuth Header
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                
                ...

    }

}
```
### EventControllerTests 에 프로퍼티 설정 추가
```java

...

import me.freelife.rest.common.AppProperties;

public class EventControllerTests extends BaseControllerTest {

    ...

    @Autowired
    AppProperties appProperties;

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
                
                ...

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // Basic OAuth Header
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        
        ...

    }

    ...
    
```