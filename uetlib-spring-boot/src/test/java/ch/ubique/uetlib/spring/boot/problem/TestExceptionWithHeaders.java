package ch.ubique.uetlib.spring.boot.problem;

import ch.ubique.uetlib.spring.boot.problem.errortype.TestErrorType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class TestExceptionWithHeaders extends AbstractThrowableProblem
        implements ThrowableProblem<TestErrorType>, ProblemResponseHeadersProvider {

    public TestExceptionWithHeaders() {
        super(TestErrorType.ERROR_TEST1, "Test Problem With Headers", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void addProblemResponseHeaders(final HttpHeaders headers) {
        headers.add("x-provider-extra-header", "provider-extra-value");
    }
}
