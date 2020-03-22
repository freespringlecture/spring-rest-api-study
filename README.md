# Account 도메인 추가
## OAuth2로 인증을 하려면 일단 Account 부터
- **id**
- **email**
- **password**
- **roels**

```java
package me.freelife.rest.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

  @Id @GeneratedValue
  private Integer id;
  private String email;
  private String password;
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<AccountRole> roles;
}
```
 
## AccountRoles enum 객체 추가
- **ADMIN**, **USER**

```java
package me.freelife.rest.accounts;

public enum AccountRole {

  ADMIN, USER
}
```

## JPA 맵핑
- `@Table(“Users”)`
 
## JPA enumeration collection mapping
하나의 **enum**만 있는 것이 아니라 여러개의 **enum**을 가질 수 있으므로 **ElementCollection**이라고 맵핑 해줘야 함  

기본으로 `LAZY` 모드인데 가져올 **ROLE**이 매우적고 **account**를 가져올때마다 필요한 정보라 `EAGER` 모드로 **Fetch**하도록 설정  
```java
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
private Set<AccountRole> roles;
```

## Event에 owner 추가
**Event**에서만 **owner**를 참조할 수 있도록 단방향 맵핑  
```java
@ManyToOne
private Account manager; //account 관리자
```