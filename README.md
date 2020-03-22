# 스프링 시큐리티 폼 인증 설정
**HttpSecurity configure** 메서드를 재정의하면 얼마든지 마음대로 스프링 시큐리티 설정이 가능함  

로그인 페이지나 여러 설정이 가능하지만 스프링 시큐리티 최신버전은 기본 폼인증 페이지를 제공  
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
  http
      .anonymous() //익명사용자 허용
          .and()
      .formLogin() //폼 인증 사용
          .and()
      .authorizeRequests() //허용할 요청
          .mvcMatchers(HttpMethod.GET, "/api/**").anonymous() // /api/ 경로의 모든걸 익명사용자에게 허용
          .anyRequest().authenticated(); // 나머지는 인증이 필요

}
```

----
- 익명 사용자 사용 활성화
- 폼 인증 방식 활성화
- 스프링 시큐리티가 기본 로그인 페이지 제공
- 요청에 인증 적용
- `/api` 이하 모든 `GET` 요청에 인증이 필요함
- (`permitAll()`을 사용하여 인증이 필요없이 익명으로 접근이 가능케 할 수 있음)
- 그밖에 모은 요청도 인증이 필요함

## 기존설정 수정
### AccountService 수정
`PasswordEncoder`를 통해 인코딩한 패스워드를 저장하도록 수정  
```java
@Autowired
PasswordEncoder passwordEncoder;

public Account saveAccount(Account account) {
  account.setPassword(this.passwordEncoder.encode(account.getPassword()));
  return this.accountRepository.save(account);
}
```

### AccountServiceTest findByUsername 테스트 수정
**accountService**로 저장하도록 수정
```java

...

@Autowired
PasswordEncoder passwordEncoder;

@Test
@TestDescription("유저 인증 테스트")
public void findByUsername() {

  ...

  this.accountService.saveAccount(account);

  ...

  // 입력한 패스워드와 DB에 저장된 패스워드와 매칭하는지 테스트하는 로직 수정
  assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
}

...

```

### AppConfig 에 테스트용 계정정보 저정하는 applicationRunner 메서드 추가
```java
@Bean
public ApplicationRunner applicationRunner() {
  return new ApplicationRunner() {
      @Autowired
      AccountService accountService;

      @Override
      public void run(ApplicationArguments args) throws Exception {
          Account freelife = Account.builder()
                  .email("freelife@gmail.com")
                  .password("freelife")
                  .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                  .build();
          accountService.saveAccount(freelife);
      }
  };
}
```