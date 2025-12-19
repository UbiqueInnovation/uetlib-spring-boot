package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public interface UetlibThrowableProblem<E extends ProblemErrorType> extends BaseUetlibThrowableProblem {

    @NonNull E errorType();
}
