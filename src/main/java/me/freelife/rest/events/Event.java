package me.freelife.rest.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.freelife.rest.accounts.Account;
import me.freelife.rest.accounts.AccountSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id; // 추가 식별자
    private String name; //이벤트 네임
    private String description; // 설명
    private LocalDateTime beginEnrollmentDateTime; //등록 시작일시
    private LocalDateTime closeEnrollmentDateTime; //종료일시    private LocalDateTime beginEventDateTime; //이벤트 시작일시
    private LocalDateTime beginEventDateTime; //이벤트 시작일시
    private LocalDateTime endEventDateTime;   //이벤트 종료일시
    private String location; // (optional) 이벤트 위치 이게 없으면 온라인 모임
    private int basePrice; // (optional) 기본 금액
    private int maxPrice; // (optional) 최고 금액
    private int limitOfEnrollment; //등록한도
    private boolean offline; // 오프라인 여부
    private boolean free; //유료 여부
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT; // 이벤트 상태
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager; //account 관리자

    //둘다 0이면 무료 아니면 무료
    public void update() {
        // Update free
        this.free = this.basePrice == 0 && this.maxPrice == 0 ? true : false;
        // Update offline
        this.offline = this.location == null || this.location.isBlank() ? false : true;
    }
}
