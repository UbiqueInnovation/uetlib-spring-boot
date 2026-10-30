package ch.ubique.uetlib.spring.boot.problem;

import ch.ubique.uetlib.spring.boot.problem.errortype.TestErrorType;
import org.springframework.http.HttpStatus;

public class TestException extends AbstractThrowableProblem
        implements ThrowableProblem<TestErrorType> {

    public TestException(final TestErrorType errorType) {
        super(errorType, "Test Problem", HttpStatus.BAD_REQUEST);
    }

    public TestException(final TestErrorType errorType, final String detail) {
        super(errorType, "Test Problem", HttpStatus.BAD_REQUEST, detail);
    }
}
