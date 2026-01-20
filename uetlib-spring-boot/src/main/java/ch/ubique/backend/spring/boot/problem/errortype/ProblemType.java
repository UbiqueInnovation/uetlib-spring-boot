package ch.ubique.backend.spring.boot.problem.errortype;

import org.jspecify.annotations.NonNull;

import java.net.URI;

public record ProblemType(@NonNull URI type, @NonNull String code) {}
