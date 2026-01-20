package ch.ubique.backend.spring.boot.problem.handler;

import static org.junit.jupiter.api.Assertions.*;

import static java.util.Objects.requireNonNull;

import ch.ubique.backend.spring.boot.problem.TestException;
import ch.ubique.backend.spring.boot.problem.errortype.TestErrorType;
import ch.ubique.backend.spring.boot.problem.handler.config.UbiqueExceptionAdviceTestConfig;

import com.google.common.base.CaseFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;
import java.util.NoSuchElementException;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UbiqueExceptionAdviceTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
class UetlibExceptionHandlerTest {

    @Autowired private TestUetlibExceptionHandler testUetlibExceptionHandler;

    private WebRequest newRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    @BeforeEach
    void beforeEach() {
        LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(Locale.GERMAN));
    }

    @Test
    void handleUetlibThrowableProblem_returnsProblemDetailWithExpectedFields() {
        final var pd1 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(TestErrorType.ERROR_TEST1), newRequest());

        // Basic fields
        assertEquals(
                TestErrorType.PROBLEM_TYPE.type()
                        + ":"
                        + CaseFormat.UPPER_UNDERSCORE.to(
                                CaseFormat.LOWER_HYPHEN, TestErrorType.ERROR_TEST1.name()),
                requireNonNull(pd1.getType()).toString());
        assertEquals("Test Problem", pd1.getTitle());
        assertEquals(400, pd1.getStatus());
        // Default detail equals reason phrase of BAD_REQUEST in our TestProblem constructor
        assertEquals("Bad Request", pd1.getDetail());
        assertNull(pd1.getInstance());

        // Custom properties added by AbstractUetlibThrowableProblem
        assertNotNull(pd1.getProperties());
        assertEquals("UT|ET1", pd1.getProperties().get("problemCode"));

        // i18n not set
        assertNull(pd1.getProperties().get("localizedTitle"));
        assertNull(pd1.getProperties().get("localizedDetail"));

        // test for error test 2
        final var pd2 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(TestErrorType.ERROR_TEST2_WITH_TRANSLATIONS, "Error Test 2 exception"), newRequest());

        assertEquals("Error Test 2 exception", pd2.getDetail());

        // i18n set for error test 2
        assertNotNull(pd2.getProperties());
        assertEquals("DE: Error Test2 Titel", pd2.getProperties().get("localizedTitle"));
        assertEquals("EN: Error Test2 Detail", pd2.getProperties().get("localizedDetail"));

        // test for error test 3
        final var pd3 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(TestErrorType.ERROR_TEST3_WITH_TITLE_ONLY), newRequest());

        // i18n title only set for error test 3
        assertNotNull(pd3.getProperties());
        assertEquals("DE: Error Test3 Titel", pd3.getProperties().get("localizedTitle"));
        assertNull(pd1.getProperties().get("localizedDetail"));
    }

    @Test
    void handleNoSuchElementException_mapsTo404ProblemDetail() throws Exception {
        final var ex = new NoSuchElementException("Element 17 not found");

        final var response = testUetlibExceptionHandler.handleMoreExceptions(ex, newRequest());

        // ResponseEntity and status
        assertInstanceOf(ResponseEntity.class, response);
        assertEquals(404, response.getStatusCode().value());

        final var body = response.getBody();
        final var problemDetail = assertInstanceOf(ProblemDetail.class, body);
        assertEquals(404, problemDetail.getStatus());
        assertEquals("Element 17 not found", problemDetail.getDetail());
        // Do not assert title/type as they are framework-defined defaults.
    }

    @Test
    void handleOtherException_isRethrown() {
        final var ex = new IllegalArgumentException("boom");

        assertThrows(
                IllegalArgumentException.class,
                () -> testUetlibExceptionHandler.handleException(ex, newRequest()));
    }
}
