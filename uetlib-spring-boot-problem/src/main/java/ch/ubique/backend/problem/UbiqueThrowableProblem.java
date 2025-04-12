package ch.ubique.backend.problem;

import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import org.zalando.problem.Problem;

@SuppressWarnings("unused")
public interface UbiqueThrowableProblem<E extends ProblemErrorType> extends Problem {

    @JsonProperty
    @NotNull
    E errorType();

    @JsonProperty
    default int errorCode() {
        return errorType().code();
    }

    /**
     * optional error id that helps the client identify which handler and potentially which action
     * should be taken.
     */
    @JsonProperty
    default String errorId() {
        return null;
    }
}
