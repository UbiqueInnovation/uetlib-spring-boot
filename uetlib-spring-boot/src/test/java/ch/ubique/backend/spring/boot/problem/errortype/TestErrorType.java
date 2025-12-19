package ch.ubique.backend.spring.boot.problem.errortype;

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
