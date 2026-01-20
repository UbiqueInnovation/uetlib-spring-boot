package ch.ubique.backend.spring.boot.problem.handler;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * To implement exception advice traits that can extend the functionality of the {@link
 * UetlibExceptionHandler} in other modules, we define this interface that has the methods from
 * {@link ResponseEntityExceptionHandler}. We have to reimplement the methods in {@link
 * UetlibExceptionHandler} because they are {@code protected} in {@link
 * ResponseEntityExceptionHandler} and interfaces only know {@code public} methods. I wish I could
 * make the traits extend {@link ResponseEntityExceptionHandler} directly, but Java does not allow
 * multiple inheritance, hence all advice traits have to be interfaces.
 */
@SuppressWarnings("JavadocReference")
public interface UetlibExceptionHandlerSkeleton {

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

    Logger logger();

    @Nullable MessageSource getMessageSource();

    /**
     * A method definition of {@link ResponseEntityExceptionHandler#createProblemDetail(Exception,
     * HttpStatusCode, String, String, Object[], WebRequest)}. Signature has to match the definition
     * exactly
     */
    ProblemDetail createProblemDetail(
            Exception ex,
            HttpStatusCode status,
            String defaultDetail,
            @Nullable String detailMessageCode,
            Object @Nullable [] detailMessageArguments,
            WebRequest request);

    /**
     * A method definition of {@link
     * ResponseEntityExceptionHandler#handleExceptionInternal(Exception, Object, HttpHeaders,
     * HttpStatusCode, WebRequest)}. Signature has to match the definition exactly
     */
    @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request);
}
