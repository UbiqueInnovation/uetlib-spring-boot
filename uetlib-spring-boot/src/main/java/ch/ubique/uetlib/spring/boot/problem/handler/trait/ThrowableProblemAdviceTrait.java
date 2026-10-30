package ch.ubique.uetlib.spring.boot.problem.handler.trait;

import ch.ubique.uetlib.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.uetlib.spring.boot.problem.ProblemResponseHeadersProvider;
import ch.ubique.uetlib.spring.boot.problem.handler.UetlibExceptionHandlerSkeletonTrait;
import org.jspecify.annotations.Nullable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public interface ThrowableProblemAdviceTrait extends UetlibExceptionHandlerSkeletonTrait {

    String PROBLEM_CODE_HEADER = "x-problem-code";

    /**
     * Handle all {@link AbstractThrowableProblem} and return the contained {@link ProblemDetail} to
     * be wrapped in a {@link org.springframework.web.ErrorResponse} by spring.
     *
     * @param problem the exception to handle
     */
    // add more here
    @ExceptionHandler(AbstractThrowableProblem.class)
    default ResponseEntity<ProblemDetail> handleAbstractThrowableProblem(
            final AbstractThrowableProblem problem,
            @SuppressWarnings("unused") final WebRequest request) {

        logException(problem, problem.status(), request);

        final var problemTypeCode = problem.type().toString().replace(":", ".");
        final var localizedTitle =
                getLocalizedProblemField("title", problemTypeCode, problem.localizedTitleArgs());
        final var localizedDetail =
                getLocalizedProblemField("detail", problemTypeCode, problem.localizedDetailArgs());

        final var body = problem.toProblemDetail(localizedTitle, localizedDetail);

        final var headers = buildErrorResponseHeaders(problem, request);

        return ResponseEntity.status(HttpStatusCode.valueOf(body.getStatus()))
                .headers(headers)
                .body(body);
    }

    private @Nullable String getLocalizedProblemField(
            final String problemField,
            final String problemTypeCode,
            final Object[] localizedProblemFieldArgs) {
        final var localizedMessageCode = "problem." + problemField + "." + problemTypeCode;
        return localizeMessage(localizedMessageCode, localizedProblemFieldArgs);
    }

    /**
     * Always adds the {@link #PROBLEM_CODE_HEADER} to the response headers and adds additional
     * headers from {@link ProblemResponseHeadersProvider} and from overriden implementations of
     * {@link #customizeAbstractThrowableProblemHeaders(AbstractThrowableProblem, WebRequest,
     * HttpHeaders)}.
     */
    private HttpHeaders buildErrorResponseHeaders(
            final AbstractThrowableProblem problem, final WebRequest request) {
        final var headers = new HttpHeaders();
        headers.add(PROBLEM_CODE_HEADER, problem.problemCode());
        if (problem instanceof final ProblemResponseHeadersProvider provider) {
            provider.addProblemResponseHeaders(headers);
        }
        customizeAbstractThrowableProblemHeaders(problem, request, headers);
        return headers;
    }

    /**
     * Customize the headers of the {@link AbstractThrowableProblem}.
     *
     * @param problem the problem to customize
     * @param request the current request
     * @param headers the headers to customize
     */
    @SuppressWarnings("unused")
    default void customizeAbstractThrowableProblemHeaders(
            final AbstractThrowableProblem problem,
            final WebRequest request,
            final HttpHeaders headers) {
        // extension hook
    }

    default @Nullable String localizeMessage(
            final @Nullable String code, final Object @Nullable [] args) {
        final var messageSource = getMessageSource();
        if (messageSource == null || code == null || args == null) {
            return null;
        }

        try {
            return messageSource.getMessage(code, args, null, LocaleContextHolder.getLocale());
        } catch (final NoSuchMessageException e) {
            logger().debug("Problem message with code '{}' not found", code, e);
            return null;
        }
    }
}
