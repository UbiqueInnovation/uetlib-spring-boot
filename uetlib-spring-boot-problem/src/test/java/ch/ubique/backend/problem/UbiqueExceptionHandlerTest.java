package ch.ubique.backend.problem;

import static org.junit.jupiter.api.Assertions.*;

import ch.ubique.backend.spring.problem.handler.UetlibExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.NoSuchElementException;

class UbiqueExceptionHandlerTest {

    private WebRequest newRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleUetlibThrowableProblem_returnsProblemDetailWithExpectedFields() {
        final var handler = new UetlibExceptionHandler();
        final var problem = new TestProblem(TestErrorType.TEST);

        final var pd = handler.handleMoreExceptions(problem, newRequest());

        // Basic fields
        assertEquals(TestProblem.TYPE, pd.getType());
        assertEquals("Test Problem", pd.getTitle());
        assertEquals(400, pd.getStatus());
        // Default detail equals reason phrase of BAD_REQUEST in our TestProblem constructor
        assertEquals("Bad Request", pd.getDetail());
        // Instance not set
        assertNull(pd.getInstance());

        // Custom properties added by AbstractUetlibThrowableProblem
        assertNotNull(pd.getProperties());
        assertEquals("TEST", pd.getProperties().get("errorType"));
        assertEquals(1, pd.getProperties().get("errorCode"));
    }

    @Test
    void handleNoSuchElementException_mapsTo404ProblemDetail() throws Exception {
        final var handler = new UetlibExceptionHandler();
        final var ex = new NoSuchElementException("missing");

        final var response = handler.handleMoreExceptions(ex, newRequest());

        // ResponseEntity and status
        assertInstanceOf(ResponseEntity.class, response);
        assertEquals(404, response.getStatusCode().value());

        final var body = response.getBody();
        final var problemDetail = assertInstanceOf(ProblemDetail.class, body);
        assertEquals(404, problemDetail.getStatus());
        assertEquals("Not Found", problemDetail.getDetail());
        // Do not assert title/type as they are framework-defined defaults.
    }

    @Test
    void handleOtherException_isRethrown() {
        final var handler = new UetlibExceptionHandler();
        final var ex = new IllegalArgumentException("boom");

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleMoreExceptions(ex, newRequest()));
    }
}
