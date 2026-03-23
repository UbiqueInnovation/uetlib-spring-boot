package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import static java.util.Comparator.comparing;

import ch.ubique.backend.spring.boot.problem.handler.UetlibExceptionHandlerSkeletonTrait;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.stream.Stream;

interface BaseViolationAdviceTrait extends UetlibExceptionHandlerSkeletonTrait {

    @Nullable
    default ResponseEntity<Object> newConstraintViolationProblemDetail(
            final Exception ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final Stream<Violation> violationStream,
            final WebRequest request) {

        final var body =
                createProblemDetail(ex, status, "Constraint violation", null, null, request);

        body.setTitle("Constraint Violation");

        final var type = URI.create("urn:ch:uetlib:problem:constraint-violation");
        body.setType(type);

        final var violations =
                violationStream
                        // sorting to make tests deterministic
                        .sorted(comparing(Violation::fieldName).thenComparing(Violation::message))
                        .toList();
        body.setProperty("violations", violations);
        if (violations.size() > 1) {
            body.setDetail("Multiple constraint violations");
        }

        return handleExceptionInternal(ex, body, headers, status, request);
    }


}
