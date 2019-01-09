package io.simondev.demoinflearnrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 이렇게 하면 free나 ID 같은 입력값은 무시하게 된다.
@Builder @NoArgsConstructor @AllArgsConstructor @Data
public class EventDto {
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
}
