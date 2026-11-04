package ch.uetlib.spring.boot.problem;

import ch.uetlib.spring.boot.problem.errortype.ProblemErrorType;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/**
 * This interface exists solely because a generic class cannot extend {@link RuntimeException}. Do
 * not use anywhere else apart from the existing usage in this module.
 */
interface BaseThrowableProblem {

    ProblemErrorType errorType();

    default URI type() {
        return errorType().type();
    }

    HttpStatusCode status();

    String title();

    String detail();

    @Nullable
    default URI instance() {
        return null;
    }

    default String problemCode() {
        return errorType().problemCode();
    }

    @Nullable
    default Boolean retryable() {
        return null;
    }
}
