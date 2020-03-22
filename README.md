# 스프링 시큐리티 OAuth 2 설정: 인증 서버 설정
**OAuth2** 서버가 설정되면 인증 토큰을 받을 수 있어야함  

스프링 **OAuth2** 6가지 인증 방법 중 **password**, **refresh_token** 두가지 방법을 지원  

## 토큰 발행 테스트
- **User**
- **Client**
- `POST /oauth/token`
  - **HTTP Basic** 인증 헤더 (클라이언트 아이디 + 클라이언트 시크릿)
  - 요청 매개변수 `(MultiValuMap<String, String>)`
    - **grant_type**: **password**
    - **username**
    - **password**
  - 응답에 **access_token** 나오는지 확인
 
## 다른 OAuth2 인증 처리 방식
보통 **account**가 인증을 하려고 하면 유저의 인증 정보를 가지고 있는 서버에게 리다이렉션이 일어나고  
토큰을 발급 받을 수 있는 또다른 토큰을 먼저 발급 받고 그다음에 토큰을 받고 그다음에 리다이렉션이 다시 일어남  

## Grant Type: Password
https://developer.okta.com/blog/2018/06/29/what-is-the-oauth2-password-grant

- 최초의 **OAuth** 토큰을 발급 받을 때는 **password**라는 **Grant Type**으로 발급 받을 것임  
- 다른 인증방식과 조금 다름 홉이 한 번 요청과 응답이 한쌍임 한 번으로 토큰을 바로 발급 받을 수 있음  
- 인증을 제공하는 서비스들이 만든 앱  
- 유저의 유저네임과 패스워드를 직접 요구 하므로 써드파티한테 이 방식을 허용하면 안되고  
  오로지 사용자 인증 정보를 보유하고 있는 서비스에서만 허용해야하는 인증방법  
- 장점은 정보를 한번에 보내면 바로 응답으로 액세스 토큰을 한번에 받을 수 있음  

- **Granty Type**: 토큰 받아오는 방법
- 서비스 오너가 만든 클라이언트에서 사용하는 **Grant Type**

### Grant Type: Password 요청시 필요한 정보
`client_id`, `client_secret`는 **basic Authentication** 형태로 **Header**에 넣음  
`grant_type=password`, `username`, `password` 는 **Request**에 파라메터로 넘겨줄 수 있음  
```
grant_type=password
&username=exampleuser
&password=1234luggage
&client_id=xxxxxxxxxx
```
 
## AuthorizationServer 설정
- `@EnableAuthorizationServer`
- `extends AuthorizationServerConfigurerAdapter`
- `configure(AuthorizationServerSecurityConfigurer security)`
  - `PassswordEncoder` 설정
- `configure(ClientDetailsServiceConfigurer clients)`
  - 클라이언트 설정
  - **grantTypes**
    - **password**
    - **refresh_token**: **OAuth Token**을 발급받을 때 **refresh_token도** 같이 발급 해주는데 이 토큰으로 새로운 엑세스 토큰을 발급받음
  - **scopes**
  - **secret** / **name**
  - `accessTokenValiditySeconds`
  - `refreshTokenValiditySeconds`
- `AuthorizationServerEndpointsConfigurer`
  - `tokenStore`
  - `authenticationManager`
  - `userDetailsService`


## 테스트 작성
기본으로 **OAuth2** 서버가 등록이 되면 `/oauth/token`을 요청할 수 있는 핸들러가 적용이 됨  

### HttpBasic를 사용하기 위해 spring-security-test 의존성 추가
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <version>${spring-security.version}</version>
    <scope>test</scope>
</dependency>
```

### 테스트 코드 작성
**HttpBasic**를 사용하기 위해선 **clientId**와 **clientSecret** 가 필요함  
```java
package me.freelife.rest.configs;

import me.freelife.rest.accounts.Account;
import me.freelife.rest.accounts.AccountRole;
import me.freelife.rest.accounts.AccountService;
import me.freelife.rest.common.BaseControllerTest;
import me.freelife.rest.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
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
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret)) // Basic OAuth Header
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}
```

### AuthServerConfig 클래스 생성
```java
package me.freelife.rest.configs;

import me.freelife.rest.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * OAuth2 인증서버 설정
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    TokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder); //client_secret를 확인할 때 사용 client_secret도 전부 Password를 Encoding해서 관리
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // inMemory 용으로 생성 원래는 jdbc로 DB로 생성해야됨
                .withClient("myApp") // myApp에 대한 클라이언트를 하나 생성
                .authorizedGrantTypes("password", "refresh_token") // 지원하는 grant_Type
                .scopes("read", "write") // 임의 값
                .secret(this.passwordEncoder.encode("pass"))
                .accessTokenValiditySeconds(10 * 60) // 엑세스 토큰의 유효시간 10분
                .refreshTokenValiditySeconds(6 * 10 * 60); // refresh_token의 유효시간
    }

    @Override //AuthenticationManager, TokenStore, UserDetailsService를 설정할 수 있음
    // 유저정보를 확인을 해야 토큰을 발급 받을 수 있음
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) //우리 유저정보를 알고 있는 authenticationManager로 설정
                .userDetailsService(accountService) // UserDetailsService 설정
                .tokenStore(tokenStore); // TokenStore 설정
    }
}
```