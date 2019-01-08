package io.simondev.demoinflearnrestapi.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // 웹과 관련된 빈이 등록
public class EventConrollerTests {

    // mocking 되어있는 Dispatcher Servlet을 상대로 가짜 요청을 만들어 보내고, 응답을 확인할 수 있다.
    // 슬라이싱 테스트: 웹과 관련된 빈만 등록해서 사용한다. (단위테스트라고 하기에는 많은 것이 개입됨)
    // 그래도 웹서버를 띄우는 것보다는 빠름
    @Autowired
    MockMvc mockMvc;

    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events") // post /api/events 요청을 보내는데
                    .contentType(MediaType.APPLICATION_JSON_UTF8) // 요청 본문에 JSON을 담아서 보낸다
                    // 좀 더 HTTP 스펙을 따르는 방법으로는 url에 확장자 비슷한 요청을 보내는 것보다는 accept header를 사용하는게 좋겠다.
                    .accept(MediaTypes.HAL_JSON)) // HAL JSON 응답을 원한다 (accpet header)
                .andExpect(status().isCreated()); // 201 응답을 예상한다.
    }

}
