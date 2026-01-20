package ch.ubique.backend.spring.boot.problem.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.ubique.backend.spring.boot.problem.handler.TestUetlibExceptionHandler;
import ch.ubique.backend.spring.boot.problem.handler.config.UbiqueExceptionAdviceTestConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest
@Import({ExceptionThrowingController.class, TestUetlibExceptionHandler.class})
@ContextConfiguration(classes = UbiqueExceptionAdviceTestConfig.class)
@ActiveProfiles("test")
@AutoConfigureJson
@AutoConfigureJsonTesters
class ExceptionThrowingControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void testExceptionThrown() throws Exception {
        testThrowEndpoint(
                status().is4xxClientError(),
                MockMvcResultMatchers.jsonPath("$.type")
                        .value(
                                "urn:ch:uetlib:spring:boot:problem:test:error-test3-with-title-only"),
                MockMvcResultMatchers.jsonPath("$.title").value("Test Problem"),
                MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Always throw TestErrorType.ERROR_TEST3_WITH_TITLE_ONLY"),
                MockMvcResultMatchers.jsonPath("$.status").value(400),
                MockMvcResultMatchers.jsonPath("$.problemCode").value("UT|ET3"),
                MockMvcResultMatchers.jsonPath("$.localizedTitle").value("DE: Error Test3 Titel"),
                MockMvcResultMatchers.jsonPath("$.localizedDetail").doesNotExist(),
                content().contentType("application/problem+json"));
    }

    protected void testThrowEndpoint(final ResultMatcher... resultMatchers) throws Exception {
        testRequest(get("/v1/throw").header(HttpHeaders.ACCEPT_LANGUAGE, "de"), resultMatchers);
    }

    protected void testRequest(
            final MockHttpServletRequestBuilder requestBuilder,
            final ResultMatcher... resultMatchers)
            throws Exception {
        mvc.perform(requestBuilder).andExpectAll(resultMatchers);
    }
}
