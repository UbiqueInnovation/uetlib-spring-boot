package ch.ubique.backend.spring.problem;

import ch.ubique.backend.spring.problem.errortype.ProblemErrorType;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public interface UetlibThrowableProblem<E extends ProblemErrorType> extends BaseUetlibThrowableProblem {

    @NonNull E errorType();
}
