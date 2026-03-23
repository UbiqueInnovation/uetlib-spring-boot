package ch.ubique.backend.spring.boot.problem.handler.trait;

import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;

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
    default ProblemDetail handleAbstractThrowableProblem(
            final AbstractThrowableProblem problem,
            @SuppressWarnings("unused") final WebRequest request) {

        final var problemTypeCode = problem.type().toString().replace(":", ".");

        final var localizedTitleCode = "problem.title." + problemTypeCode;
        final var localizedTitleArgs = problem.localizedTitleArgs();
        final var localizedTitle = localizeMessage(localizedTitleCode, localizedTitleArgs);

        final var localizedDetailCode = "problem.detail." + problemTypeCode;
        final var localizedDetailArgs = problem.localizedDetailArgs();
        final var localizedDetail = localizeMessage(localizedDetailCode, localizedDetailArgs);

        return problem.toProblemDetail(localizedTitle, localizedDetail);
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
