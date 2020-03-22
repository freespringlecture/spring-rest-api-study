# 스프링 부트 2.2.5 버전으로 업데이트
현재 스프링 부트 최신 권장 버전은 2.2.5.RELEASE
https://spring.io/projects/spring-boot

## 스프링 부트 버전을 올리면 바뀔 수 있는 일
-	기본 (자동) 설정 값 변경
-	의존성 변경
-	기존 애플리케이션의 동작이 바뀌거나 컴파일 에러가 발생할 수 있다

## 스프링 부트 2.2.* 주요 변화
-	**JUnit 4** -> **JUnit 5**
-	스프링 **HATEOAS** 버전이 올라가면서 스프링 **HATEOAS**의 **API**가 바뀌었다

## 스프링 HATEOAS
-	**Resource** -> **EntityModel**
-	**Resources** -> **CollectionModel**
-	**PagedResrouces** -> **PagedModel**
-	**ResourceSupport** -> **RepresentationModel**
-	`assembler.toResource` -> `assembler.toModel`
-	`org.springframework.hateoas.mvc.ControllerLinkBuilder` -> `org.springframework.hateoas.server.mvc.WebMvcLinkBuilder`
-	**MediaTypes** 중에 (**UTF8**)인코딩이 들어간 상수 제거.

## JUnit 5
https://junit.org/junit5/docs/current/user-guide/

https://www.baeldung.com/junit-5

-	`org.junit` -> `org.junit.jupiter`
-	`@Ignore` -> `@Disabled`
-	`@Before`, `@After` -> `@BeforeEach`, `@AfterEach`
-	`@TestDescription` (우리가 만든거) -> `@DisplayName`

## 스프링 MVC 변경
표준모델이 아니라 제거됨
-	**MediaType** 중에 (**UTF8**)인코딩이 들어간 상수 deprecation


## 스프링 부트 2.2.5.RELEASE 업데이트 적용 소스코드
### pom.xml 파일의 프레임워크 버전 업데이트
- **spring-boot-starter-parent 2.2.5.RELEASE** 업데이트
- **jdk 14** 업데이트
- **modelmapper 2.3.6** 업데이트
- **spring-security-oauth2-autoconfigure 2.2.5.RELEASE** 업데이트
- **JunitParams** 제거
- **asciidoctor-maven-plugin** 버전 제거
- **spring-restdocs-asciidoctor** 버전 제거
- **maven-resources-plugin** 버전 제거

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.5.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>me.freelife</groupId>
    <artifactId>spring-rest-api-study</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-rest-api-study</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>14</java.version>
        <modelmapper.version>2.3.6</modelmapper.version>
        <spring-security-oauth2-autoconfigure.version>2.2.5.RELEASE</spring-security-oauth2-autoconfigure.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-hateoas</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
            <version>${modelmapper.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth.boot</groupId>
            <artifactId>spring-security-oauth2-autoconfigure</artifactId>
            <version>${spring-security-oauth2-autoconfigure.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <!--<scope>test</scope>-->
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.restdocs</groupId>
            <artifactId>spring-restdocs-mockmvc</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <version>${spring-security.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.asciidoctor</groupId>
                <artifactId>asciidoctor-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>generate-docs</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>process-asciidoc</goal>
                        </goals>
                        <configuration>
                            <backend>html</backend>
                            <doctype>book</doctype>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.restdocs</groupId>
                        <artifactId>spring-restdocs-asciidoctor</artifactId>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-resources</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>
                                ${project.build.outputDirectory}/static/docs
                            </outputDirectory>
                            <resources>
                                <resource>
                                    <directory>
                                        ${project.build.directory}/generated-docs
                                    </directory>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### ErrorResource
- **Resource** -> **EntityModel** 로 변경
- `linkTo`, `methodOn` 패키지 변경
  `org.springframework.hateoas.mvc.ControllerLinkBuilder` -> `org.springframework.hateoas.server.mvc.WebMvcLinkBuilder`

```java
...

import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {

...

```

### EventController
- **MediaTypes.HAL_JSON_UTF8_VALUE** -> **MediaTypes.HAL_JSON_VALUE** 로 변경
- `linkTo` 패키지 변경
  `org.springframework.hateoas.mvc.ControllerLinkBuilder` -> `org.springframework.hateoas.server.mvc.WebMvcLinkBuilder`
-	`assembler.toResource` -> `assembler.toModel` 메서드 변경
- `ControllerLinkBuilder` -> `var` 로 변경

```java
...

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

...

@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

  ...

  var selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());

  ...

  var pagedResources = assembler.toModel(page, e -> new EventResource(e));

  ...

```

### EventResource
- **Resource** -> **EntityModel** 로 변경
- `linkTo`, `methodOn` 패키지 변경
  `org.springframework.hateoas.mvc.ControllerLinkBuilder` -> `org.springframework.hateoas.server.mvc.WebMvcLinkBuilder`

```java

...

import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    ...

```

### IndexController
-	**ResourceSupport** -> **RepresentationModel** 클래스 변경
- `linkTo` 패키지 변경
  `org.springframework.hateoas.mvc.ControllerLinkBuilder` -> `org.springframework.hateoas.server.mvc.WebMvcLinkBuilder`

```java

...

import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

  ...

  public RepresentationModel index() {
    var index = new RepresentationModel();

  ...

```

### BaseTest
공통 테스트 클래스 
- `@RunWith(SpringRunner.class)` 어노테이션 제거
-	`@Ignore` -> `@Disabled` 어노테이션 변경

### AccountServiceTest
- `org.junit.Test` -> `org.junit.jupiter.api.Test` 패키지 변경
-	`@TestDescription` -> `@DisplayName` 어노테이션 변경
- `@Rule`, `ExpectedException` 제거
- 예외타입 `org.junit.jupiter.api.Assertions.assertThrows` 로 처리
- **BaseTest** 적용

```java

...

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest extends BaseTest {

...

    @Test
    @DisplayName("없는 사용자의 경우 예상되는 예외로 타입과 메세지 확인")
    public void findByUsernameFail() {
        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername("random@email.com"));
    }
}
```


### AuthServerConfigTest
- `org.junit.Test` -> `org.junit.jupiter.api.Test` 패키지 변경
-	`@TestDescription` -> `@DisplayName` 어노테이션 변경

### EventControllerTests
-	`@Before` -> `@BeforeEach` 어노테이션 변경
- `org.junit.Test` -> `org.junit.jupiter.api.Test` 패키지 변경
-	`@TestDescription` -> `@DisplayName` 어노테이션 변경
- **MediaType.APPLICATION_JSON_UTF8** -> **MediaType.APPLICATION_JSON** 변경
- **MediaTypes.HAL_JSON_UTF8_VALUE** -> **MediaTypes.HAL_JSON_VALUE** 변경

### EventTest
https://www.baeldung.com/parameterized-tests-junit-5

**junit 5**의 `@ParameterizedTest`를 사용하도록 변경 기존 **JUnitParams** 라이브러리와 코드 제거
`@MethodSource`에 파라메터 메서드를 셋팅해서 테스트

```java
package me.freelife.rest.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest
    @MethodSource("parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    //parametersFor prefix가 테스트 메서드를 찾아서 자동으로 파라메터로 사용을 해줌
    private static Stream<Arguments> parametersForTestFree() {
        return Stream.of(
            Arguments.of(0, 0, true),
            Arguments.of(100, 0, false),
            Arguments.of(0, 100, false),
            Arguments.of(100, 200, false)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForTestOffline")
    public void testOffline(String location, boolean isOffline) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Stream<Arguments> parametersForTestOffline() {
        return Stream.of(
            Arguments.of("강남", true),
            Arguments.of(null, false),
            Arguments.of("           ", false)
        );
    }

}
```

### IndexControllerTest
- `org.junit.Test` -> `org.junit.jupiter.api.Test` 패키지 변경

### TestDescription
**junit 5**의 `@DisplayName` 어노테이션과 동일한 역할을 하는 기능이므로 제거

### ApplicationTests
불필요한 테스트 클래스 제거