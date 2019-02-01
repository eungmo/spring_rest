package io.simondev.demoinflearnrestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
//@WebMvcTest // 웹과 관련된 빈이 등록
// 웹 쪽 관련된 테스트는 @SpringBootTest로 통합 테스트하는게 편하다
// 왜냐하면 모킹할 것이 너무 많기 때문이다.
// 때문에 테스트하기도 힘들고, 코드 개발 시 테스트 코드를 너무 자주 바꿔야 한다.
@SpringBootTest
@AutoConfigureMockMvc
// REST Docs를 설정
@AutoConfigureRestDocs
// 커스터마이징한 설정을 적용
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
// 이 클래스는 테스트를 가지고 있지 않는 테스트
@Ignore
public class BaseControllerTest {
    // mocking 되어있는 Dispatcher Servlet을 상대로 가짜 요청을 만들어 보내고, 응답을 확인할 수 있다.
    // 슬라이싱 테스트: 웹과 관련된 빈만 등록해서 사용한다. (단위테스트라고 하기에는 많은 것이 개입됨)
    // 그래도 웹서버를 띄우는 것보다는 빠름
    @Autowired
    protected MockMvc mockMvc;

    // 스프링 부트를 사용할 때, Mapping Jackson Json이 의존성으로 들어가 있으면,
    // ObjectMapper를 자동으로 Bean 등록해준다.
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;
}
