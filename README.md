# 스프링 REST Docs: 링크, (Req, Res) 필드와 헤더 문서화
## 요청 필드 문서화
- requestFields() + fieldWithPath()
- responseFields() + fieldWithPath()
- requestHeaders() + headerWithName()
- responseHedaers() + headerWithName()
- links() + linkWithRel()

## 테스트 할 것
- API 문서 만들기
  - 요청 본문 문서화: request-body
  - 응답 본문 문서화: response-body
  - 링크 문서화: links
    - self
    - query-events
    - update-event
    - profile 링크 추가
  - 요청 헤더 문서화: requestHeaders
  - 요청 필드 문서화: requestFields
  - 응답 헤더 문서화: responseHeaders
  - 응답 필드 문서화: responseFields

## Relaxed 접두어
> Relaxed 접두어를 사용하지 않고 전부 다 문서화 하는 것을 권장  
> API가 변경되었을 때 변경을 테스트가 감지해서 적절하게 문서도 바뀐 API코드에 맞춰서 업데이트를 할 수있으므로  
- 장점: 문서 일부분만 테스트 할 수 있다
- 단점: 정확한 문서를 생성하지 못한다