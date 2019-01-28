# PostgreSQL 적용
테스트 할 때는 계속 H2를 사용해도 좋지만 애플리케이션 서버를 실행할 때 PostgreSQL을
사용하도록 변경하자  

### `/scripts.md` 참고
#### 1. PostgreSQL 드라이버 의존성 추가
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```
#### 2. 도커로 PostgreSQL 컨테이너 실행
```bash
docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
  ```
#### 3. 도커 컨테이너에 들어가보기
```bash
docker exec -i -t rest bash
su - postgres
psql -d postgres -U postgres
\l
\dt
  ```
#### 4. 데이터소스 설정
> application.properties  
```
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```
#### 5. 하이버네이트 설정
> application.properties  
```properties
## 하이버네이트 DDL 설정
spring.jpa.hibernate.ddl-auto=create-drop
## 경고 무시 postgresql 드라이버가 createClob() 메서드를 구현하지 않아 경고문구가 출력됨
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

## 콘솔에 JPA를 통해 실행된 쿼리를 표시
spring.jpa.show_sql=true
## 콘솔에 표시되는 쿼리를 좀 더 가독성 있게 표시
spring.jpa.properties.hibernate.format_sql=true
## 하이버네이트 SQL 로그 보기
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
## 콘솔에 표시되는 쿼리문 위에 어떤 실행을 하려는지 HINT를 표시
spring.jpa.properties.hibernate.use_sql_comments : true
```

## 애플리케이션 설정과 테스트 설정 중복 어떻게 줄일 것인가?
### @SpringBootTest 시 테스트 문제
> @SpringBootTest 이므로 통합테스트라서 @SpringBootApplication에 등록되는 모든 빈들을 등록하게 됨  
> 그래서 테스트가 Postgresql을 사용하게 되는 문제점이 있음  

### 설정 파일이 같을 시 설정 중복 문제 발생
> 설정파일을 같게 해서 test/resources 에 application.properties 파일을 생성해  
> 테스트 전용 설정파일을 만들어 줄 수 있지만 파일명이 같으면 main 의 properties 파일을 테스트 properties 파일이 덮어 써버려서  
> 설정하지 않은 값은 모두 없어지므로 모든 설정값을 중복으로 설정해주어야 되는 문제가 있음  

### 프로파일과 @ActiveProfiles 활용하여 설정 중복문제 해결
> 아래의 설정 사항을 적용시켜줄 테스트 클래스에 `@ActiveProfiles("test")` 라고 명시해주면  
> 기존 설정파일에 더해서 test 설정 사항도 적용시켜주므로 테스트 설정에 적용한대로 테스트 할 수 있음  
> 공용 설정사항은 application 테스트에 해당하는 것은 application-test에 관리하면  
> properties의 중복 문제를 해결 할 수있음  

#### application-test.properties
```
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```