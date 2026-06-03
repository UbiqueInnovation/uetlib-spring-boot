package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.TestErrorType;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class TestExceptionWithHeaders extends AbstractThrowableProblem
        implements ThrowableProblem<TestErrorType>, ProblemResponseHeadersProvider {

    public TestExceptionWithHeaders() {
        super(TestErrorType.ERROR_TEST1, "Test Problem With Headers", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void addProblemResponseHeaders(final HttpHeaders headers) {
        headers.add("x-problem-code", problemCode());
    }
}
