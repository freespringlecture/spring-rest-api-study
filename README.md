# 이벤트 비지니스 로직
> 이벤트를 만들고 등록/수정/조회하는 기능까지만 다룸  
> 이번강좌에서는 경매 선착순 등록은 다루지 않음  

#### basePrice와 maxPrice 경우의 수와 각각의 로직
  
| basePrice | maxPrice | Description                                                  |
| --------- | -------- | ------------------------------------------------------------ |
| 0         | 100      | 선착순 등록                                                  |
| 0         | 0        | 무료                                                         |
| 100       | 0        | 무제한 경매 (높은 금액 낸 사람이 등록)                       |
| 100       | 200      | 제한가 선착순 등록<br /><br />처음부터 200을 낸사람은 선 등록.<br /><br />100을 내고 등록할 수 있으나 더 많이 낸 사람에 의해 밀려날 수 있음. |
  
## 결과 값
- **id: 이벤트 고유 식별자**
- name
- ...
- **eventStatus: DRAFT​​**, PUBLISHED, ENROLLMENT_STARTED, ...
  - DRAFT​​: 아직 이벤트가 본인한테만 보이는 상태
  - PUBLISHED: 다른 사람들이 이벤트를 볼 수 있고 접수기간이라면 접수받을 수 있음
- offline
  - location이 있으면 오프라인 없으면 온라인
- free: 유/무료 여부
- _links: HATEOAS 정보
  - profile (for the self-descriptive message): 메세지 자체에 대한 정보를 담고 있는 문서 링크를 담고 있음
  - self: 생성하는 이벤트를 조회하는 링크
  - publish
  - ...