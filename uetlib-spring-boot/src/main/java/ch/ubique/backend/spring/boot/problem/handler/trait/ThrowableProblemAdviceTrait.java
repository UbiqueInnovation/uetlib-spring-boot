package ch.ubique.backend.spring.boot.problem.handler.trait;

import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.backend.spring.boot.problem.ProblemResponseHeadersProvider;
import ch.ubique.backend.spring.boot.problem.handler.UetlibExceptionHandlerSkeletonTrait;

import org.jspecify.annotations.Nullable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public interface ThrowableProblemAdviceTrait extends UetlibExceptionHandlerSkeletonTrait {

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

        final var problemTypeCode = problem.type().toString().replace(":", ".");
        final var localizedTitle = getLocalizedProblemField(problem, "title", problemTypeCode);
        final var localizedDetail = getLocalizedProblemField(problem, "detail", problemTypeCode);

        final var body = problem.toProblemDetail(localizedTitle, localizedDetail);

        final var headers = buildErrorResponseHeaders(problem, request);

        return ResponseEntity.status(HttpStatusCode.valueOf(body.getStatus()))
                .headers(headers)
                .body(body);
    }

    private @Nullable String getLocalizedProblemField(
            final AbstractThrowableProblem problem,
            final String titleDetail,
            final String problemTypeCode) {
        final var localizedTitleCode = "problem." + titleDetail + "." + problemTypeCode;
        final var localizedTitleArgs = problem.localizedTitleArgs();
        return localizeMessage(localizedTitleCode, localizedTitleArgs);
    }

    private HttpHeaders buildErrorResponseHeaders(
            final AbstractThrowableProblem problem, final WebRequest request) {
        final var headers = new HttpHeaders();
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
            logger().warn("Problem message with code '{}' not found", code, e);
            return null;
        }
    }
}
