package ch.ubique.backend.spring.boot.problem.springdoc;

import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.backend.spring.boot.problem.ThrowableProblem;
import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.springframework.http.ProblemDetail;

import java.net.URI;

/**
 * This interface only exists for springdoc documentation purposes. All custom problems should be
 * documented with an abstract class extending this class. E.g.:
 *
 * <p>
 *
 * {@snippet lang=java:
 * public abstract class ExampleProblem
 *         extends SpringDocProblem<ExampleErrorType, ExampleException> {}
 * }
 *
 * Then, use this class in the controller, not {@link ProblemDetail}, to have {@link #type()},
 * {@link #status()}, {@link #title()}, {@link #detail()}, {@link #errorType()} and {@link
 * #errorCode()} documented as required. This class represents the documentation equivalent of all
 * exceptions extending {@link AbstractThrowableProblem}. SpringDoc renders the correct error types
 * using the customizer {@link UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer} as
 * auto-registered in {@link UetlibSpringBootProblemSpringDocAutoConfiguration}
 *
 * <p>This extends the specification for RFC-9457 problem details with uetlib-specific extensions:
 * `errorType` and `errorCode` and marks most fields as required.
 *
 * @see ThrowableProblem
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC-9457</a>
 * @param <E>
 */
@Schema(
        description =
                """
                        Base documentation interface for all dedicated custom RFC-9457 problems.
                        Each (`errorCode`) has to represent an application-wide 1:1 mapping to (`type`, `errorType`).
                        Meaning, `errorCode` is unique application-wide, but `errorType` will only be unique for this problem `type`""")
@SuppressWarnings("unused")
public interface SpringDocProblem<
        E extends Enum<E> & ProblemErrorType, P extends RuntimeException & ThrowableProblem<E>> {

    @NotNull
    @JsonProperty(required = true)
    URI type();

    @JsonProperty(required = true)
    int status();

    @NotNull
    @JsonProperty(required = true)
    String title();

    @NotNull
    @JsonProperty(required = true)
    String detail();

    @JsonProperty
    URI instance();

    @NotNull
    @JsonProperty(required = true)
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorCode for this specific Problem. Non-null iff errorCode is set")
    E errorType();

    @JsonProperty(required = true)
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorType for this specific Problem. Non-null iff errorType is set")
    int errorCode();
}
