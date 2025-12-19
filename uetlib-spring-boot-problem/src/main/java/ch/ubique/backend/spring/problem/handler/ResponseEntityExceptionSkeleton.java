package ch.ubique.backend.spring.problem.handler;

import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * To implement exception advice traits that can extend the functionality of the {@link
 * UetlibExceptionAdvice} in other modules, we define this interface that has the methods from
 * {@link ResponseEntityExceptionHandler}. We have to reimplement the methods in {@link
 * UetlibExceptionAdvice} because they are {@code protected} in {@link
 * ResponseEntityExceptionHandler} and interfaces only know {@code public} methods. I wish I could
 * make the traits extend {@link ResponseEntityExceptionHandler} directly, but Java does not allow
 * multiple inheritance, hence all advice traits have to be interfaces.
 */
public interface ResponseEntityExceptionSkeleton {

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
