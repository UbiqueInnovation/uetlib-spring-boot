package ch.uetlib.spring.boot.problem.errortype;

import java.net.URI;

public record ProblemType(URI type, String code) {}
