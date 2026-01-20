package ch.ubique.backend.spring.boot.problem.handler;

import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public interface ThrowableProblemAdviceTrait extends UetlibExceptionHandlerSkeleton {

    /**
     * Handle all {@link AbstractThrowableProblem} and return the contained {@link ProblemDetail} to
     * be wrapped in a {@link org.springframework.web.ErrorResponse} by spring.
     *
     * @param problem the exception to handle
     */
    // add more here
    @ExceptionHandler(AbstractThrowableProblem.class)
    default ProblemDetail handleAbstractThrowableProblem(
            final AbstractThrowableProblem problem,
            @SuppressWarnings("unused") final WebRequest request) {

        final var problemTypeUnderscore = problem.type().toString().replaceAll(":", "_");

        final var localizedTitleCode = "problem.title." + problemTypeUnderscore;
        final var localizedTitleArgs = problem.localizedTitleArgs();
        final var localizedTitle = localizeMessage(localizedTitleCode, localizedTitleArgs);

        final var localizedDetailCode = "problem.detail." + problemTypeUnderscore;
        final var localizedDetailArgs = problem.localizedDetailArgs();
        final var localizedDetail = localizeMessage(localizedDetailCode, localizedDetailArgs);

        return problem.toProblemDetail(localizedTitle, localizedDetail);
    }
}
