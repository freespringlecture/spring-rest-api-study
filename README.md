# 스프링 REST Docs: 문서 빌드
## 스프링 REST Docs
https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
### pom.xml에 메이븐 플러그인 설정
```xml
<plugin> 
  <groupId>org.asciidoctor</groupId>
  <artifactId>asciidoctor-maven-plugin</artifactId>
  <version>1.5.7.1</version>
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
      <version>2.0.2.RELEASE</version>
    </dependency>
  </dependencies>
</plugin>
<plugin> 
	<artifactId>maven-resources-plugin</artifactId>
	<version>3.1.0</version>
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
```

#### 템플릿 파일 추가
https://gitlab.com/whiteship
https://raw.githubusercontent.com/keesun/study/master/rest-api-with-spring/src/main/asciidoc/index.adoc
  - src/main/asciidoc/index.adoc

## 문서 생성하기
> mvn package 해주면 target/generated-docs, target/classes/static/docs 에 index.html 이 생성됨  
- mvn package
  - test
  - prepare-package :: process-asciidoc
  - prepare-package :: copy-resources
- 문서확인
  > 아래의 경로에서 확인 가능  
  - /docs/index.html

## 생성 과정 설명
### index.html 생성
> asciidoctor-maven-plugin이 패키징할때 prepare-package에 process-asciidoc을 처리하라고 함  
> package라는 maven goal을 실행할때 asciidoctor-maven-plugin이 제공하는  
> process-asciidoc 이라는 기능이 실행이 된거고 이 기능은 기본적으로 src/main/asciidoc 안에 들어있는  
> 모든 asciidoc 문서를 html로 만들어줌  

### target/classes/static/docs 경로에 카피
> maven-resources-plugin의 기능 중에 copy-resources라는 기능을 prepare-package에 끼워넣음  
> 순서가 중요함 asciidoctor-maven-plugin 다음에 maven-resources-plugin를 처리해야함  
> copy는 resources/resource/directory 의 디렉토리의 모든 파일을 outputDirectory로 카피해줌  

### 스프링 부트 정적 리소스 지원 기능
> build된 디렉토리 기준으로 static 디렉토리 안에 있으면 서버에서 리소스 접근이 가능  

### profile 추가
```java
eventResource.add(new Link("/docs/index.html").withRel("profile"));
```

## 테스트 할 것
- API 문서 만들기
  - 요청 본문 문서화
  - 응답 본문 문서화
  - 링크문서화
    - self
    - query-events
    - update-event
    - profile 링크 추가
  - 요청 헤더 문서화
  - 요청 필드 문서화
  - 응답 헤더 문서화
  - 응답 필드 문서화
