# Account 도메인 추가
## OAuth2로 인증을 하려면 일단 Account 부터
- id
- email
- password
- roels
 
## AccountRoles
- ADMIN, USER
 
## JPA 맵핑
- @Table(“Users”)
 
## JPA enumeration collection mapping
> 하나의 enum만 있는 것이 아니라 여러개의 enum을 가질 수 있으므로 ElementCollection이라고 맵핑 해줘야 함  
> 기본으로 LAZY 모드인데 가져올 ROLE이 매우적고 account를 가져올때마다 필요한 정보라 EAGER 모드로 Fetch하도록 설정  
```java
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
private Set<AccountRole> roles;
```

## Event에 owner 추가
> Event에서만 owner를 참조할 수 있도록 단방향 맵핑  
```java
@ManyToOne
Account manager;
```