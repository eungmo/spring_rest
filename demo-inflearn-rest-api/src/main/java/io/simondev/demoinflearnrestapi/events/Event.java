package io.simondev.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.simondev.demoinflearnrestapi.accounts.Account;
import io.simondev.demoinflearnrestapi.accounts.AccountSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime; // 등록 시작 일시
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime; // 이벤트 종료 일시
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    // Enum타입은 기본으로 스트링으로 바꿔준다
    // 기본 값은 ORDINAL이고, 따라서 0부터 숫자로 저장되는데,
    // 혹시 나중에 Enum이 추가, 수정, 삭제, 순서가 바뀌면 데이터가 꼬이기 때문에
    // 문자열로 저장하는게 좋다.
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;
    // 이벤트에서만 Owner를 참조할 수 있도록 단방향 맵핑을 한다.
    @ManyToOne
    // 이 클래스의 객체를 JSON Serialize할 때 이 Serializer를 사용하도록 한다.
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    public void update() {
        // Update free
        if (basePrice == 0 && maxPrice == 0) {
            free = true;
        } else {
            free = false;
        }

        // Update offline
        if (location == null || location.isBlank()) {
            offline = false;
        } else {
            offline = true;
        }
    }
}
