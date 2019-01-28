# 스프링 부트 프로젝트 만들기
## 추가할 의존성
- Web
- JPA
- HATEOAS
- REST Docs
- H2
- PostgreSQL
- Lombok

## 자바 버전 11로 시작
- [자바는 여전히 무료다](https://medium.com/@javachampions/java-is-still-free-c02aef8c9e04)

## 스프링 부트 핵심 원리
- 의존성 설정 (pom.xml)
- 자동 설정 (@EnableAutoConfiguration)
- 내장웹서버(의존성과자동설정의일부)
- 독립적으로 실행 가능한 JAR (pom.xml의 플러그인)

- default 는 compile scope
- `<optional>` 프로젝트를 참조하고 있는 다른 프로젝트에 추이적으로 의존성이 추가되지 않음

### Lombok 의존성 추가
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
  
> 최초 실행 시 scope를 테스트라고 해두면 테스트 시에만 사용 Application 실행 시 postgresql로 실행하므로 오류가 남  
> 런타임시 별다른 데이터베이스 설정이 없는경우 In-memory DB인 H2를 기본을 설정하게 되어있음  