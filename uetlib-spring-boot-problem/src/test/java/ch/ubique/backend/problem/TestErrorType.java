package ch.ubique.backend.problem;

import ch.ubique.backend.spring.problem.errortype.ProblemErrorType;

public enum TestErrorType implements ProblemErrorType {
    TEST(1);

    private final int code;

    TestErrorType(final int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }
}
