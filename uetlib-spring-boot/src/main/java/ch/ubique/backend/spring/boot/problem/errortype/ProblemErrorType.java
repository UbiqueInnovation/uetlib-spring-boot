package ch.ubique.backend.spring.boot.problem.errortype;

import com.google.common.base.CaseFormat;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.net.URI;

/** Usually implemented by enums. These also have the {@link #name()} method by default. */
public interface ProblemErrorType extends Serializable {

    @NonNull ProblemType problemType();

    /**
     * The actual resulting type of this specific error type: The problem type (as base) joined with
     * the kebab-cased name of this specific error type
     *
     * <p>Example: {@code urn:ch:uetlib:spring:boot:problem:test:test-error}
     */
    default @NonNull URI type() {
        return URI.create(
                problemType().type()
                        + ":"
                        + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name()));
    }

    /**
     * The actual resulting code of this specific error type: The problem type code (as base) joined
     * with the code of this specific error type
     *
     * <p>Example: {@code UT|ET}
     */
    default @NonNull String problemCode() {
        return problemType().code() + "|" + code();
    }

    @NonNull String name();

    @NonNull String code();
}
