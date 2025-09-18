package ch.ubique.backend.problem.springdoc;

import ch.ubique.backend.problem.UbiqueThrowableProblem;
import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;

/**
 * This interface only exists for springdoc documentation purposes, do not use it in actual code
 *
 * @param <E>
 */
@SuppressWarnings("unused")
public interface SpringDocProblem<
                E extends Enum<E> & ProblemErrorType,
                P extends AbstractThrowableProblem & UbiqueThrowableProblem<E>>
        extends Problem {

    @Nullable
    @JsonProperty
    E errorType();

    @Nullable
    @JsonProperty
    Integer errorCode();

    /**
     * This is just for Springdoc to show a required `int` for `status` instead of the `StatusType`
     * model
     */
    @NotNull
    @JsonProperty
    int status();
}
