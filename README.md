# 스프링 시큐리티 기본 설정
  - 스프링 시큐리티가 의존성에 들어 있으면 스프링 부트는 스프링 시큐리티용 자동설정을 적용해줌  
  - 그 자동설정은 모든 요청은 다 인증을 필요로 하게되고 스프링 시큐리티가 사용자를 임의로 인메모리로 하나 만들어줌  
  
  ## 시큐리티 필터를 적용하기 않음
  - **/docs/index.html**
  
  ## 로그인 없이 접근 가능
  - **GET /api/events**
  - **GET /api/events/{id}**
   
  ## 로그인 해야 접근 가능
  - 나머지 다
  - **POST /api/events**
  - **PUT /api/events/{id}**
  - ...
   
  ## 스프링 시큐리티 OAuth 2.0
  - **AuthorizationServer**: **OAuth2** 토큰 발행(`/oauth/token`) 및 토큰 인증(`/oauth/authorize`)
    - **Oder** 0 (리소스 서버 보다 우선 순위가 높다.)
  - **ResourceServer**: 리소스 요청 인증 처리 (**OAuth2** 토큰 검사)
    - **Oder** 3 (이 값은 현재 고칠 수 없음)
   
  ## 스프링 시큐리티 설정
  - `@EnableWebSecurity`
  - `@EnableGlobalMethodSecurity`
  
  ### `extends WebSecurityConfigurerAdapter`
  - **SecurityConfig** 클래스 설정  
  - 설정하는 순간 스프링 부트가 제공하는 스프링 시큐리티 자동설정은 더이상 제공되지 않음  
  - 하지만 `WebSecurityConfigurerAdapter` 클래스를 상속 받는 순간 모든 요청은 인증을 필요로 하게됨  
  ```java
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
  }
  ```
  
  ### PasswordEncoder: PasswordEncoderFactories.createDelegatingPassworkEncoder()
  - 스프링 시큐리티 최신 버전에 추가된 `PasswordEncoder`  
  - 다양한 인코딩 타입을 지원하며 인코딩된 어떠한 방식으로 인코딩된 건지 알 수 있도록 패스워드 앞에 **prefix**를 붙여줌  
  - **prefix**값에 따라 적절한 인코더를 적용해서 패스워드 값이 매칭되는지 확인  
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  ```
  
  ### TokenStore
  **OAuth** 토큰을 저장 `InMemoryTokenStore` 사용  
  ```java
  @Bean
  public TokenStore tokenStore() {
      return new InMemoryTokenStore();
  }
  ```
  
  ### `AuthenticationManagerBean`
  다른 **AuthorizationServer**나 **ResourceServer**가 참조할 수 있도록 오버라이딩 해서 빈으로 등록  
  ```java
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
  }
  ```
  
  ### `configure(AuthenticationManagerBuidler auth)`
  **AuthenticationManager** 재정의  
  - `userDetailsService`: **accountService** 적용
  - `passwordEncoder`
    ```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }
    ```
  
  ### configure(WebSecurty web)
  - **HTTP**를 적용하기 전에 시큐리티 필터를 적용할지 말지를 먼저 결정  
  - 스프링 시큐리티를 필터를 사용하기 전에 적용된 패턴을 다 걸러냄  
  - 서버가 일을 조금이라도 일을 덜하게 하기 위해 정적인 리소스들은 웹 필터로 걸러주는 것을 권장  
  
  - **ignore**
    - `/docs/**`
    - `/favicon.ico`
  
  ### `PathRequest.toStaticResources()`
  스프링 부트가 제공하는 **static** 리소스들의 기본위치를 다 가져와서 스프링 시큐리티에서 제외  
  ```java
  @Override
  public void configure(WebSecurity web) throws Exception {
      web.ignoring().mvcMatchers("/docs/index.html");
      web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }
  ```
  
  ### configure(HttpSecurity http)
  - 스프링 시큐리티는 적용하되 **HTTP**로 거르는 방법  
  - 요청에 필요로 하는 인증이 **anonymous**면 아무나 접근할 수 있는 요청이 됨  
  - **authorize Request** 할때 **anonymous**는 허용하라고 해주면 허용이 됨  
  - 스프링 시큐리티 필터를 타고 모든 필터링을 서버가 처리  
  - `/docs/**`: **permitAll**
  
  
  ## 스프링 시큐리티 설정 구현 전체 로직
  
  ### AppConfig 클래스 생성
  **Application** 클래스에 있던 `modelMapper()` 를 **AppConfig** 클래스로 이동
  ```java
  package me.freelife.rest.configs;
  
  import org.modelmapper.ModelMapper;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.security.crypto.factory.PasswordEncoderFactories;
  import org.springframework.security.crypto.password.PasswordEncoder;
  
  @Configuration
  public class AppConfig {
  
      @Bean
      public ModelMapper modelMapper() {
          return new ModelMapper();
      }
  
      @Bean
      public PasswordEncoder passwordEncoder() {
          return PasswordEncoderFactories.createDelegatingPasswordEncoder();
      }
  }
  ```
  
  ### SecurityConfig 클래스 구현
  ```java
  package me.freelife.rest.configs;
  
  import me.freelife.rest.accounts.AccountService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
  import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.security.authentication.AuthenticationManager;
  import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
  import org.springframework.security.config.annotation.web.builders.HttpSecurity;
  import org.springframework.security.config.annotation.web.builders.WebSecurity;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.security.oauth2.provider.token.TokenStore;
  import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
  
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
      @Autowired
      AccountService accountService;
  
      @Autowired
      PasswordEncoder passwordEncoder;
  
      @Bean
      public TokenStore tokenStore() {
          return new InMemoryTokenStore();
      }
  
      @Bean
      @Override
      public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
      }
  
      @Override
      protected void configure(AuthenticationManagerBuilder auth) throws Exception {
          auth.userDetailsService(accountService)
                  .passwordEncoder(passwordEncoder);
      }
  
      @Override
      public void configure(WebSecurity web) throws Exception {
          web.ignoring().mvcMatchers("/docs/index.html");
          web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
      }
  }
  ```
  
  ### application.properties 에 스프링 시큐리티 로그 설정 추가
  ```properties
  logging.level.org.springframework.security=DEBUG
  ```