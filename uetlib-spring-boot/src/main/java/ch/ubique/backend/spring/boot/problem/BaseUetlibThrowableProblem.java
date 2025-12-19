package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/**
 * This interface exists solely because a generic class cannot extend {@link RuntimeException}. Do
 * not use anywhere else apart from the existing usage in this module. Please.
 */
public interface BaseUetlibThrowableProblem {

    @NonNull URI type();

    @NonNull HttpStatusCode status();

    @NonNull String title();

    @NonNull String detail();

    @Nullable
    default URI instance() {
        return null;
    }

    @NonNull ProblemErrorType errorType();

    default int errorCode() {
        return errorType().code();
    }
}
