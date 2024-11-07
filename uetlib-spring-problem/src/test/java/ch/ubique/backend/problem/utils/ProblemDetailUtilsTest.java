package ch.ubique.backend.problem.utils;

import static org.junit.jupiter.api.Assertions.*;

import ch.ubique.backend.problem.ProblemDetailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Map;

class ProblemDetailUtilsTest {

    @Test
    public void testToThrowableProblem() {
        final var problem1 =
                Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .withTitle("Not Found")
                        .withDetail("Resource not found")
                        .withType(URI.create("https://example.com/not-found"))
                        .withInstance(URI.create("https://example.com/resource"))
                        .with("key", "value")
                        .build();
        final var problemDetail1 =
                new ProblemDetailBuilder()
                        .status(HttpStatus.NOT_FOUND)
                        .detail("Resource not found")
                        .title("Not Found")
                        .type(URI.create("https://example.com/not-found"))
                        .instance(URI.create("https://example.com/resource"))
                        .properties(Map.of("key", "value"))
                        .build();
        assertProblemMatches(problem1, problemDetail1);

        final var problem2 =
                Problem.builder()
                        .withStatus(Status.BAD_REQUEST)
                        .withTitle("Bad Request")
                        .withDetail("Invalid request")
                        .build();
        final var problemDetail2 =
                new ProblemDetailBuilder()
                        .status(HttpStatus.BAD_REQUEST)
                        .title("Bad Request")
                        .detail("Invalid request")
                        .build();
        assertProblemMatches(problem2, problemDetail2);

        final var problem3 =
                Problem.builder()
                        .withStatus(Status.INTERNAL_SERVER_ERROR)
                        .withTitle("Server Error")
                        .withType(URI.create("about:blank"))
                        .with("additional", "info")
                        .build();
        final var problemDetail3 =
                new ProblemDetailBuilder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .title("Server Error")
                        .properties(Map.of("additional", "info"))
                        .build();
        assertProblemMatches(problem3, problemDetail3);

        final var problem4 =
                Problem.builder()
                        .withStatus(Status.OK)
                        .withTitle("OK")
                        .withType(URI.create("about:blank"))
                        .build();
        final var problemDetail4 = new ProblemDetailBuilder().status(HttpStatus.OK).build();
        assertProblemMatches(problem4, problemDetail4);
    }

    void assertProblemMatches(final Problem problem, final ProblemDetail problemDetail) {
        final var convertedProblem = ProblemDetailUtils.toThrowableProblem(problemDetail);
        assertEquals(problem.getType(), convertedProblem.getType());
        assertEquals(problem.getTitle(), convertedProblem.getTitle());
        assertEquals(problem.getDetail(), convertedProblem.getDetail());
        assertEquals(problem.getInstance(), convertedProblem.getInstance());
        assertEquals(problem.getStatus(), convertedProblem.getStatus());
        assertEquals(problem.getInstance(), convertedProblem.getInstance());
    }
}
