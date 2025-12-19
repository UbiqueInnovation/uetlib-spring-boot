package ch.ubique.backend.spring.problem.handler;

import ch.ubique.backend.spring.problem.AbstractUetlibThrowableProblem;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public interface ThrowableProblemAdviceTrait {

    /**
     * Handle all {@link AbstractUetlibThrowableProblem} and return the contained {@link
     * ProblemDetail} to be wrapped in a {@link org.springframework.web.ErrorResponse} by spring.
     *
     * @param problem the exception to handle
     */
    // add more here
    @ExceptionHandler(AbstractUetlibThrowableProblem.class)
    default ProblemDetail handleThrowableProblems(
            final AbstractUetlibThrowableProblem problem,
            @SuppressWarnings("unused") final WebRequest request) {
        return problem.toProblemDetail();
    }
}
