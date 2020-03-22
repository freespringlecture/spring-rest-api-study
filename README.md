# 스프링 시큐리티 현재 사용자
궁극적인 목적은 현재 사용자를 **Account**로 받아오고 아니면 `null`  

## SecurityContext
자바 **ThreadLocal** 기반 구현으로 인증 정보를 담고 있다

### 인증 정보 꺼내는 방법: 
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
```

### AccountService의 UserDetails의 구현체가 리턴해주는 스프링 시큐리티 유저를 받아 올 수 있음
해당 **username**으로 해당하는 유저를 **DB**에서 조회해올 수 있음  
```java
User principal = (User) authentication.getPrincipal();
```

## `@AuthenticationPrincipal` `spring.security.User` user
스프링 시큐리티가 제공해주는 기능 중에 하나로 스프링 **MVC** 핸들러 파라메터에 `@AuthenticationPrincipal`를 사용하면 `getPrincipal()` 로 리턴 받을 수 있는 객체를 바로 주입받을 수가 있음

있는 지 없는지만 확인하는 용도로 사용  
- 인증 안한 경우에 `null`
- 인증 한 경우에는 **username**과 **authorities** 참조 가능

## queryEvents를 호출 할때 user가 있는 경우 create-event 링크 추가
```java
if(user != null) {
    // 유저가 있으면 EventController에 create-event 링크를 추가
    pagedResources.add(linkTo(EventController.class).withRel("create-event"));
}
```

## 이벤트를 생성할때 account 유저가 필요
이벤트를 생성할때 현재 사용자 정보를 이벤트에 주입을 해줘야 함 **Event**에 있는 **manager**를 셋팅해주려면  
현재 **user**가 **springsecurity user**가 아니라 **account** 여야함   
 
## `spring.security.User`를 상속받는 클래스를 구현하면
**AccountService**의 **UserDetails**의 구현체가 리턴해주는 스프링 시큐리티 유저를 **Account**객체를 알고있는 객체로 바꿔야함  
- 도메인 **User**를 받을 수 있다
- `@AuthenticationPrincipa` `me.freelife.user.UserAdapter`
- `Adatepr.getUser().getId()`

## AccountAdapter 구현
스프링 시큐리티 유저를 **Account**객체를 알고있는 객체로 바꿔주는 클래스
```java
package me.freelife.rest.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdpter extends User {

    private Account account;

    public AccountAdpter(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    public Account getAccount() {
        return account;
    }
}
```

## AccountAdapter로 리턴 하도록 수정
**AccountService**의 **UserDetails**를 `AccountAdapter`로 리턴하도록 수정  

이렇게 수정하면 이제 **Controller**에서 `AccountAdapter`를 받을 수 있음  
```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username)); // account 객체가 없으 UsernameNotFoundException 에러를 던짐
    return new AccountAdapter(account);
}
```

## AccountAdapter 에서 유저정보 꺼내오기
**DB**에 접근하지 않고도 현재 사용자의 정보에 접근할 수 있으므로 이벤트를 생성할 때 현재 매니저를 셋팅할 수 있게 됨  

### `@AuthenticationPrincipal` 로 AccountAdapter 에서 현재 유저 꺼내오기
```java
@AuthenticationPrincipal AccountAdapter currentUser
```

```java
@GetMapping
public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler, @AuthenticationPrincipal AccountAdapter currentUser) {
    //현재 사용자 인증정보 가져오기
    Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

    Page<Event> page = this.eventRepository.findAll(pageable);
    var pagedResources = assembler.toResource(page, e -> new EventResource(e));
    //profile로 가는 Link를 추가
    pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
    if(currentUser != null) {
        // 유저가 있으면 EventController에 create-event 링크를 추가
        pagedResources.add(linkTo(EventController.class).withRel("create-event"));
    }
    return ResponseEntity.ok(pagedResources);
}
```

## SpEL을 사용해서 Account 정보만 꺼내옴
**AccountAdapter**에서 `account`라는 필드 값을 꺼내서 주입 해줌  
```java
@AuthenticationPrincipal(expression=”account”) Account account
```

### CurrentUser Annotation을 만들어서 Annotation 간추리기
```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "account")
public @interface CurrentUser {
}
```

## 커스텀 애노테이션을 만들면
**anonymousUser**인 경우에는 **Authentication** 객체가 들고 있던 **Principal**이 문자열 '**anonymousUser**' 임  

**Account** 객체를 가지고 있는 **AccountAdapter** 객체가 아니라 에러가 발생함  

### getEvent 컨트롤러로 확인
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
```

### `@CurrentUser` 애노테이션 표현식 수정
현재 인증 정보가 **anonymousUse** 인 경우에는 `null`을 보내고 아니면 “**account**”를 꺼내준다  

### `@CurrentUser` Account account
```java
package me.freelife.rest.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}
```

## manager 정보를 생성
**EventController**에서 **Event**를 생성할 때 **manager** 정보를 생성  
  
## 조회 API 개선
현재 조회하는 사용자가 **owner**인 경우에 **update** 링크 추가 (**HATEOAS**)  

### Event 한건 조회 API에 update 링크 추가
`@CurrentUser` **Account currentUser** 를 받아와서 현재의 **event manager**와 동일하면 업데이트 링크 추가  
```java
if(event.getManager().equals(currentUser)) {
    eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
}
```
 
## 수정 API 개선
현재 사용자가 이벤트 **owner**가 아닌 경우에 403 에러 발생  

### Event 수정 API에 예외처리
**manager** 와 현재 유저정보가 다를 경우 인가되지 않았으므로 `HttpStatus.UNAUTHORIZED` 리턴 처리  
```java
if(!existingEvent.getManager().equals(currentUser)){
    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
}
```

## EventController 개선된 로직 전체
```java

...

import me.freelife.rest.accounts.Account;
import me.freelife.rest.accounts.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    ...

    /**
     * Event Create API
     * @param eventDto
     * @param errors
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {

        ...

        event.update(); //저장하기 전에 유료인지 무료인지 여부 업데이트
        event.setManager(currentUser); //manager 정보 생성

        ...

    }

    /**
     * Event List Select API
     * @param pageable
     * @param assembler
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler, @CurrentUser Account account) {
        //현재 사용자 인증정보 가져오기
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

        ...

        if(account != null) {
            // 유저가 있으면 EventController에 create-event 링크를 추가
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    /**
     * Event One Select API
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ...

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if(event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {
        
        ...

        Event existingEvent = optionalEvent.get();
        if(!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        ...

    }

```

## EventControllerTests 에 테스트 코드 추가
```java
@Test
@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기 인증정보 가져오기")
public void queryEventsWithAuthentication() throws Exception {
    //Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When & Then
    this.mockMvc.perform(get("/api/events")
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .param("page", "1")
            .param("size", "10")
            .param("sort", "name,DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andExpect(jsonPath("_links.create-event").exists())
            .andDo(document("query-events"))
    ;
}
```