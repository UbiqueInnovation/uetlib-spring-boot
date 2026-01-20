package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.TestErrorType;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public class TestException extends AbstractThrowableProblem
        implements ThrowableProblem<TestErrorType> {

    public TestException(final @NonNull TestErrorType errorType) {
        super(errorType, "Test Problem", HttpStatus.BAD_REQUEST);
    }

    public TestException(final @NonNull TestErrorType errorType, final @NonNull String detail) {
        super(errorType, "Test Problem", HttpStatus.BAD_REQUEST, detail);
    }
}
