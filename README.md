# 이벤트 도메인 구현

## Event 생성 API
- 다음의 입력 값을 받는다
  - name
  - description
  - beginEnrollmentDateTime
  - closeEnrollmentDateTime
  - beginEventDateTime
  - endEventDateTime
  - location (optional) 이게 없으면 온라인 모임
  - basePrice (optional)
  - maxPrice (optional)
  - limitOfEnrollment

## Lombok 설치
1. Lombok plugin 설치
2. Build, Excution, Depolyment - Annotation Processors - Enable annotation processing

## Builder
- 내가 입력한 문자열이 무슨 입력값인지 알기 쉬움
- 점으로 연결되서 코딩하기도 쉬움
- Builder 로는 기본생성자가 생성이 안됨(모든 파라메터를 다 가지고 있는 생성자만 생성)
- public이 아니고 default 생성자로 생성이 되어 다른 패키지에서 이 이벤트에 대한 객체를 만들기 애매함

## Lombok 애노테이션 설정
- @AllArgsConstructor @NoArgsConstructor @Getter @Setter
- 기본 생성자와 파라메터를 모두 가진 생성자 그리고 getter, setter을 만들기 위해 어노테이션 설정
- Lombok 애노테이션은 Meta애노테이션으로 동작하지 않기 때문에 연관되는 애노테이션을 다 합쳐서 줄여서 사용할 수 없음
```java
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {}
```

### @EqualsAndHashCode(of = "id")
- id 값만으로 EqualsAndHashCode를 비교하라 라는 의미
- 필요하다면 다른 필드를 더 추가할 수도 있지만 다른 Entity와의 묶음을 만드는 것은 좋지 않음
- Equals 와 HashCode를 구현할 때 모든 필드를 기본적으로 다 사용함
- 나중에 Entity 간에 연관관계가 있을 때 상호참조하는 관계가 되면
- EqualsAndHashCode를 구현한 코드 안에서 서로간의 메소드를 계속 호출하다가 스택오버플로우가 발생할 수도 있음

### @Data
- 모든 애노테이션을 자동으로 설정해주는 @Data라는 애노테이션은 Entity 위에서 사용하는 것을 권장하지 않음
- @EqualsAndHashCode 를 모든 필드를 다 사용해서 구현하기 때문에 상호참조 때문에 스택오버플로우 문제가 발생할 수 있으므로 권장하지 않음