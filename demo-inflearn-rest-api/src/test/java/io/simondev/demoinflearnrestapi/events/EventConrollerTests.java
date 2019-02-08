package io.simondev.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.simondev.demoinflearnrestapi.accounts.Account;
import io.simondev.demoinflearnrestapi.accounts.AccountRepository;
import io.simondev.demoinflearnrestapi.accounts.AccountRole;
import io.simondev.demoinflearnrestapi.accounts.AccountService;
import io.simondev.demoinflearnrestapi.common.AppProperties;
import io.simondev.demoinflearnrestapi.common.BaseControllerTest;
import io.simondev.demoinflearnrestapi.common.RestDocsConfiguration;
import io.simondev.demoinflearnrestapi.common.TestDescription;
import jdk.jfr.ContentType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventConrollerTests extends BaseControllerTest {

    // @WebMvcTest 애노테이션은 슬라이싱 테스트이고, 따라서 웹 관련 빈만 등록된다.
    // 즉, repository는 등록이 되지 않기 때문에 MockBean을 등록할 필요가 있다.
    //@MockBean
    //EventRepository eventRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    // 인메모리 DB이긴 하지만 테스트가 돌고있는 중에는 DB를 공유하기 때문에
    // 데이터가 테스트 간에 독립적이지 않다.
    // 따라서, setUp에서 데이터를 지우거나,
    // 데이터를 생성할 때마다 주요한 키 값이 되는 것들은 랜덤한 값으로 사용해야 한다.
    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

/*
    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        // 제대로된 요청을 만들어보자
        EventDto event = EventDto.builder()
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
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        // Mocking 테스트가 아니기 때문에, EventDTO을 거쳐서 들어가고, free, offline, id값은 위에 입력한 값으로 들어가지 않게된다.

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
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }
    */

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
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
                .free(true)
                .offline(false)
                .id(100)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400 Bad Request 응답을 반환
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 12, 9, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 11, 9, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 01, 10, 9, 30))
                .endEventDateTime(LocalDateTime.of(2019, 01, 9, 9, 30))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("Woodlands Bizhub")
                .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists()) // 에러 배열들이 나온다 Object Name
                //.andExpect(jsonPath("$[0].field").exists()) // 어떤 필드에서 발생한 것인가 (필드에러에만 존재)
                .andExpect(jsonPath("content[0].defaultMessage").exists()) // 기본 메시지
                .andExpect(jsonPath("content[0].code").exists()) // 에러 코드
                //.andExpect(jsonPath("$[0].rejectedValue").exists()) // 입력을 거절당한, 에러를 발생시킨 값 (필드 에러에만 존재)
                .andExpect(jsonPath("_links.index").exists()) // 404 에러 발생 시, index 링크를 제공하는 지 확인 (로고 버튼 같은 것)
        ;
    }

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent_Business_Logic() throws Exception {
        EventDto event = EventDto.builder()
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

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("free").value(false)) // 가격이 정해졌기 때문에, false
                .andExpect(jsonPath("offline").value(true)) // location이 정해졌기 때문에, true
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        // 링크 정보 snippet로 추가
                        links(
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("query-events").description("Link to query event"),
                                linkWithRel("update-event").description("Link to update an existing event"),
                                linkWithRel("profile").description("Link to profile")
                        ),
                        // Request 헤더를 snippet로 추가
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description(" limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header: creating a new event url"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type: HAL JSON")
                        ),
                        // relaxed prefix를 사용하면, 모든 부분을 문서활 필요가 없어진다.
                        //relaxedResponseFields(
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description(" limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("manager").description("manager of new event"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of new event"),
                                fieldWithPath("_links.self.href").description("link to the self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer "+ getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password")
        );

        MockHttpServletResponse response = perform.andReturn().getResponse();
        String responseBody = response.getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        // Given
        Account user = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(user);
    }

    @Test
    @TestDescription("인증되지 않은 사용자가 30개의 이벤트를 10개씩 볼 때, 두 번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given : 이벤트 30개
        IntStream.range(0, 30).forEach(this::generateEvent); // 메서드 레퍼런스로 간결하게
        // Same: Lambda
        //IntStream.range(0, 30).forEach(i -> {
        //    this.generateEvent(i);
        //});
        // Same2: Loop
        //for (int i = 0; i < 30; i++) {
        //    this.generateEvent(i);
        //}

        // When : 조회한다
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size","10")
                    .param("sort", "name,DESC")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                )

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to go to the first page"),
                                linkWithRel("prev").description("link to go to the previous page"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to go to the next page"),
                                linkWithRel("last").description("link to go to the last page"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("page").description("the page number"),
                                parameterWithName("size").description("the number of items in a page"),
                                parameterWithName("sort").description("sort of items")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type: HAL JSON")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[].id").description("each item's id"),
                                fieldWithPath("_embedded.eventList[].name").description("each item's name"),
                                fieldWithPath("_embedded.eventList[].description").description("each item's description"),
                                fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("each item's begin enrollment datetime"),
                                fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("each item's close enrollment datetime"),
                                fieldWithPath("_embedded.eventList[].beginEventDateTime").description("each item's begin event datetime"),
                                fieldWithPath("_embedded.eventList[].endEventDateTime").description("each item's end event datetime"),
                                fieldWithPath("_embedded.eventList[].location").description("each item's location"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("each item's base price"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("each item's max price"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("each item's limit of enrollment"),
                                fieldWithPath("_embedded.eventList[].offline").description("each item's offline"),
                                fieldWithPath("_embedded.eventList[].free").description("each item's free"),
                                fieldWithPath("_embedded.eventList[].eventStatus").description("each item's event status"),
                                fieldWithPath("_embedded.eventList[].manager").description("each item's manager"),
                                fieldWithPath("_embedded.eventList[]._links.self.href").description("each item's self link"),
                                fieldWithPath("_links.first.href").description("link to the first page"),
                                fieldWithPath("_links.prev.href").description("link to the previous page"),
                                fieldWithPath("_links.self.href").description("link to the self"),
                                fieldWithPath("_links.next.href").description("link to the next page"),
                                fieldWithPath("_links.last.href").description("link to the last page"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("page.size").description("the number of items in a page"),
                                fieldWithPath("page.totalElements").description("the number of items"),
                                fieldWithPath("page.totalPages").description("the number of page"),
                                fieldWithPath("page.number").description("the page number")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("인증된 사용자가 30개의 이벤트를 10개씩 볼 때, 두 번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        // Given : 이벤트 30개
        IntStream.range(0, 30).forEach(this::generateEvent); // 메서드 레퍼런스로 간결하게

        // When : 조회한다
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .param("page", "1")
                .param("size","10")
                .param("sort", "name,DESC")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event generateEvent(int index) {
        Event event = buildEvent(index);

        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
                    .name("Event " + index)
                    .description("Test Event")
                    .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 9, 9, 30))
                    .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 10, 9, 30))
                    .beginEventDateTime(LocalDateTime.of(2019, 01, 11, 9, 30))
                    .endEventDateTime(LocalDateTime.of(2019, 01, 12, 9, 30))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("Woodlands Bizhub")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // Given : 이벤트 하나 생성
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event",
                        links(
                                linkWithRel("self").description("Link to Self"),
                                linkWithRel("profile").description("Link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type: HAL JSON")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description(" limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of new event"),
                                fieldWithPath("_links.self.href").description("link to the self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("없는 이벤트 하나를 조회했을 때, 404 응답받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/{id}", 1111111111))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Account account = this.createAccount();
        Event event = this.generateEvent(200, account);

        // modelMapper를 사용해 event를 eventDto에 담는다.
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("Link to Self"),
                                linkWithRel("profile").description("Link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description(" limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type: HAL JSON")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of the event"),
                                fieldWithPath("name").description("Name of the event"),
                                fieldWithPath("description").description("description of the event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of the event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of the event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of the event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of the event"),
                                fieldWithPath("location").description("location of the event"),
                                fieldWithPath("basePrice").description("base price of the event"),
                                fieldWithPath("maxPrice").description("max price of the event"),
                                fieldWithPath("limitOfEnrollment").description(" limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("manager").description("manager of the event"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of the event"),
                                fieldWithPath("_links.self.href").description("link to the self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        // Given
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        // Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        // Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/1111111")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
