package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.TestErrorType;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class TestProblem extends AbstractUetlibThrowableProblem
        implements UetlibThrowableProblem<TestErrorType> {

    public static final URI TYPE = URI.create("urn:ch:uetlib:spring:boot:problem:test");

    private final TestErrorType errorType;

    public TestProblem(final @NonNull TestErrorType errorType) {
        super(TYPE, "Test Problem", HttpStatus.BAD_REQUEST);
        this.errorType = errorType;
    }

    @Override
    public @NonNull TestErrorType errorType() {
        return errorType;
    }
}
