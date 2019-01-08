package io.simondev.demoinflearnrestapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void builderTest() {
        // 빌더가 있는지 체크하자
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // 자바 빈 스펙을 준수하는지 체크하자 (디폴트 생성자와 세터 사용)
        // 이를 위해서 @AllArgsConstructor @NoArgsConstructor
        //  @Getter @Setter @EqualsAndHashCode(of = "id")를 이벤트 클래스에 추가해야 한다.

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
}