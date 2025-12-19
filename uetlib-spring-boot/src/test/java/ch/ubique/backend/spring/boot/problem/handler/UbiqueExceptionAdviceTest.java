package ch.ubique.backend.spring.boot.problem.handler;

import static org.junit.jupiter.api.Assertions.*;

import ch.ubique.backend.spring.boot.problem.TestProblem;
import ch.ubique.backend.spring.boot.problem.errortype.TestErrorType;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.NoSuchElementException;

class UbiqueExceptionAdviceTest {

    private WebRequest newRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleUetlibThrowableProblem_returnsProblemDetailWithExpectedFields() {
        final var handler = new UetlibExceptionHandler();
        final var problem = new TestProblem(TestErrorType.TEST);

        final var pd = handler.handleThrowableProblems(problem, newRequest());

        // Basic fields
        assertEquals(TestProblem.TYPE, pd.getType());
        assertEquals("Test Problem", pd.getTitle());
        assertEquals(400, pd.getStatus());
        // Default detail equals reason phrase of BAD_REQUEST in our TestProblem constructor
        assertEquals("Bad Request", pd.getDetail());
        assertNull(pd.getInstance());

        // Custom properties added by AbstractUetlibThrowableProblem
        assertNotNull(pd.getProperties());
        assertEquals("TEST", pd.getProperties().get("errorType"));
        assertEquals(1, pd.getProperties().get("errorCode"));
    }

    @Test
    void handleNoSuchElementException_mapsTo404ProblemDetail() throws Exception {
        final var handler = new UetlibExceptionHandler();
        final var ex = new NoSuchElementException("Element 17 not found");

        final var response = handler.handleMoreExceptions(ex, newRequest());

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
        final var handler = new UetlibExceptionHandler();
        final var ex = new IllegalArgumentException("boom");

        assertThrows(
                IllegalArgumentException.class, () -> handler.handleException(ex, newRequest()));
    }
}
