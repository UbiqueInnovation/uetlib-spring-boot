package ch.ubique.backend.problem.errortype;

import jakarta.validation.constraints.NotNull;

@SuppressWarnings("NullableProblems")
public interface ProblemErrorType {
    @NotNull
    String name();

    @NotNull
    int code();
}
