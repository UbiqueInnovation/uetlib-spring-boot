package ch.ubique.backend.spring.problem.errortype;

import jakarta.validation.constraints.NotNull;

/** Usually implemented by enums. These also have the {@link #name()} mathod by default. */
public interface ProblemErrorType {
    @NotNull
    String name();

    int code();
}
