package io.simondev.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // 웹과 관련된 빈이 등록
public class EventConrollerTests {

    // mocking 되어있는 Dispatcher Servlet을 상대로 가짜 요청을 만들어 보내고, 응답을 확인할 수 있다.
    // 슬라이싱 테스트: 웹과 관련된 빈만 등록해서 사용한다. (단위테스트라고 하기에는 많은 것이 개입됨)
    // 그래도 웹서버를 띄우는 것보다는 빠름
    @Autowired
    MockMvc mockMvc;

    // 스프링 부트를 사용할 때, Mapping Jackson Json이 의존성으로 들어가 있으면,
    // ObjectMapper를 자동으로 Bean 등록해준다.
    @Autowired
    ObjectMapper objectMapper;

    // @WebMvcTest 애노테이션은 슬라이싱 테스트이고, 따라서 웹 관련 빈만 등록된다.
    // 즉, repository는 등록이 되지 않기 때문에 MockBean을 등록할 필요가 있다.
    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        // 제대로된 요청을 만들어보자
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 9, 9, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 10, 9, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 01, 11, 9, 30))
                .endEventDateTime(LocalDateTime.of(2019, 01, 12, 9, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("Woodlands Bizhub")
                .build();

        // 이 것을 하지 않으면 NullPointException이 발생한다
        // 왜냐하면, 컨트롤러에서 repository를 하여 save를 하더라도,
        // null을 반환하기 때문이다. 그리고 null에서 getID()를 하니 NullPointException이 발생한다.
        // 그래서 우리는 스터빙을 해줘야 한다.
        // 즉, 테스트를 하기 위해서는 save를 했을 때, 이벤트를 리턴하라고 구체적인 동작을 명시해야한다.
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events") // post /api/events 요청을 보내는데
                    .contentType(MediaType.APPLICATION_JSON_UTF8) // 요청 본문에 JSON을 담아서 보낸다
                    // 좀 더 HTTP 스펙을 따르는 방법으로는 url에 확장자 비슷한 요청을 보내는 것보다는 accept header를 사용하는게 좋겠다.
                    .accept(MediaTypes.HAL_JSON) // HAL JSON 응답을 원한다 (accept header)
                    .content(objectMapper.writeValueAsString(event))) // event 객체를 JSON 문자열로 바꿔서 요청 본문에 넣어준다.
                .andDo(print())
                .andExpect(status().isCreated()) // 201 응답을 예상한다.
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) // Location이 있는지
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE)) // Content-type이 맞는지
        ;
    }
}
