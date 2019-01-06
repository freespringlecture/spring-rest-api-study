package me.freelife.rest.events;

import lombok.*;

import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    private Integer id; // 추가 식별자
    private String name; //이벤트 네임
    private String description; // 설명
    private LocalDateTime beginEnrollmentDateTime; //등록 시작일시
    private LocalDateTime closeEnrollmentDateTime; //종료일시    private LocalDateTime beginEventDateTime; //이벤트 시작일시
    private LocalDateTime endEventDateTime;   //이벤트 종료일시
    private String location; // (optional) 이벤트 위치 이게 없으면 온라인 모임
    private int basePrice; // (optional) 기본 금액
    private int maxPrice; // (optional) 최고 금액
    private int limitOfEnrollment; //등록한도
    private boolean offline; // 오프라인 여부
    private boolean free; //유료 여부
    private EventStatus eventStatus; // 이벤트 상태
}
