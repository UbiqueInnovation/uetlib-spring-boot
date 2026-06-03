package ch.ubique.backend.spring.boot.problem.springdoc;

import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.backend.spring.boot.problem.ThrowableProblem;
import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;
import ch.ubique.backend.spring.boot.problem.springdoc.autoconfigure.UetlibSpringBootProblemSpringDocAutoConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

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
 * {@link #status()}, {@link #title()}, {@link #detail()} and {@link #problemCode()} documented as
 * required. This class represents the documentation equivalent of all exceptions extending {@link
 * AbstractThrowableProblem}. SpringDoc renders the correct error types using the customizer {@link
 * UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer} as auto-registered in {@link
 * UetlibSpringBootProblemSpringDocAutoConfiguration}
 *
 * <p>This extends the specification for RFC-9457 problem details with uetlib-specific extensions:
 * {@code localizedTitle}/{@code localizedDetail} and {@code problemCode} and marks most fields as
 * required.
 *
 * @see ThrowableProblem
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC-9457</a>
 * @param <E>
 */
@Schema(
        description =
                """
                Base documentation interface for all dedicated custom RFC-9457 problems.
                Each (`problemCode`) has to represent an application-wide 1:1 mapping to a `type`.\
                """)
@SuppressWarnings("unused")
public interface SpringDocProblem<
        E extends Enum<E> & ProblemErrorType, P extends RuntimeException & ThrowableProblem<E>> {

    @JsonProperty(required = true)
    URI type();

    @JsonProperty(required = true)
    @Schema(example = "400")
    int status();

    @JsonProperty(required = true)
    String title();

    @JsonProperty(required = true)
    String detail();

    @JsonProperty
    @Schema(example = "/path/to/endpoint/that/has/thrown")
    URI instance();

    @JsonProperty(required = true)
    @Schema(
            description =
                    "Corresponds 1:1 to a type for this specific Problem, should be shown to the"
                            + " user for debugging purposes")
    String problemCode();

    @JsonProperty
    @Schema(
            description =
                    "If set, represents a localized error title than can be shown to the user")
    String localizedTitle();

    @JsonProperty
    @Schema(
            description =
                    "If set, represents a localized error detail than can be shown to the user")
    String localizedDetail();
}
