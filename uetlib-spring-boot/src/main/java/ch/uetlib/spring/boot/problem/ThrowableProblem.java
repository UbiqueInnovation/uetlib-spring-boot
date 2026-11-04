package ch.uetlib.spring.boot.problem;

import ch.uetlib.spring.boot.problem.errortype.ProblemErrorType;

@SuppressWarnings("unused")
public interface ThrowableProblem<E extends ProblemErrorType> extends BaseThrowableProblem {}
