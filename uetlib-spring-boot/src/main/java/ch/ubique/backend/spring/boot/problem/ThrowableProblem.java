package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

@SuppressWarnings("unused")
public interface ThrowableProblem<E extends ProblemErrorType> extends BaseThrowableProblem {}
