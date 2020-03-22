# Events API 개선: 출력값 제한하기
- 이전에 작업한 `@CurrentUser` 애노테이션을 이용해 **manager** 정보를 생성해주는데  
- **email** 이외에 패스워드와 권한 까지 모두 노출되는 문제가 있음  
- 지난 번에는 **ErrorSerializer**에서 `@JsonComponent`를 사용해서 **ObjectMapper**에 등록이 되도록 했었음  
- `@JsonCommponent` 를 사용하지 않고 **Serializer** 처리  

```json
{
  "id" : 4,
  "name" : "test 3PISM1Ju",
  "description" : "test event",
  ...
  "free" : false,
  "eventStatus" : "DRAFT",
  "owner" : {
  "id" : 3,
  "email" : "keesun@email.com",
  "password" : "{bcrypt}$2a$10$3z/rHmeYsKpoOQR3aUq38OmZjZNsrGfRZxSnmpLfL3lpLxjD5/JZ6",
  "roles" : [ "USER", "ADMIN" ]
},
```

## 생성 API 개선
- **Event owner** 설정
- 응답에서 **owner**의 **id**만 보내 줄 것 

### `JsonSerializer<User>` 구현
**JsonSerializer** 를 상속 받아 **account**의 **id**만 보내주도록 **AccountSerializer** 구현  
```java
package me.freelife.rest.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {
    @Override
    public void serialize(Account account, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", account.getId());
        gen.writeEndObject();
    }
}
```

### Event 객체에 `@JsonSerialize(using)` 설정
**Event**를 **Serialization** 할때는 **manager**를 **AccountSerializer**로 **Serialization** 하도록 설정  
```java

...

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.freelife.rest.accounts.AccountSerializer;

@ManyToOne
@JsonSerialize(using = AccountSerializer.class)
private Account manager; //account 관리자

...

```