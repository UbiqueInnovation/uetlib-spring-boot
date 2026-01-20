package ch.ubique.backend.spring.boot.problem;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/**
 * This interface exists solely because a generic class cannot extend {@link RuntimeException}. Do
 * not use anywhere else apart from the existing usage in this module. Please.
 */
public interface BaseThrowableProblem {

    @NonNull URI type();

    @NonNull HttpStatusCode status();

    @NonNull String title();

    @NonNull String detail();

    @Nullable
    default URI instance() {
        return null;
    }

    @JsonIgnore
    @NonNull String problemCode();
}
