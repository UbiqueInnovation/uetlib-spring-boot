package ch.ubique.backend.problem.springdoc;

import ch.ubique.backend.problem.UbiqueThrowableProblem;
import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

/**
 * This interface only exists for springdoc documentation purposes, all custom problems should
 * extend this class, not {@link SpringDocRfc9457Problem} directly, to have {@link #type()}, {@link
 * #status()}, {@link #title()}, {@link #detail()}, {@link #errorType()} and {@link #errorCode()}
 * documented as required. This class represents the documentation equivalent of {@link
 * UbiqueThrowableProblem}
 *
 * <p>This extends the specification for RFC-9457 problem details with ubique-specific extensions:
 * `errorType` and `errorCode` and marks most fields as required.
 *
 * @see SpringDocRfc9457Problem
 * @see UbiqueThrowableProblem
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
                E extends Enum<E> & ProblemErrorType,
                P extends AbstractThrowableProblem & UbiqueThrowableProblem<E>>
        extends SpringDocRfc9457Problem {

    @NotNull
    @JsonProperty(required = true)
    URI type();

    @JsonProperty(required = true)
    Integer status();

    @NotNull
    @JsonProperty(required = true)
    String title();

    @NotNull
    @JsonProperty(required = true)
    String detail();

    @NotNull
    @JsonProperty(required = true)
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorCode for this specific Problem. Non-null iff errorCode is set")
    E errorType();

    @NotNull
    @JsonProperty(required = true)
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorType for this specific Problem. Non-null iff errorType is set")
    Integer errorCode();
}
