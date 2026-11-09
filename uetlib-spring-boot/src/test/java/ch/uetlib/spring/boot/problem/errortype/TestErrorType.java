package ch.uetlib.spring.boot.problem.errortype;

import java.net.URI;

public enum TestErrorType implements ProblemErrorType {
    ERROR_TEST1("ET1"),
    ERROR_TEST2_WITH_TRANSLATIONS("ET2"),
    ERROR_TEST3_WITH_TITLE_TRANSLATION_ONLY("ET3");

    public static final ProblemType PROBLEM_TYPE =
            new ProblemType(
                    URI.create("urn:ch:uetlib:spring:boot:problem:test"), "UT" /* UETLIB-TEST */);

    private final String code;

    TestErrorType(final String code) {
        this.code = code;
    }

    @Override
    public ProblemType problemType() {
        return PROBLEM_TYPE;
    }

    @Override
    public String code() {
        return code;
    }
}
