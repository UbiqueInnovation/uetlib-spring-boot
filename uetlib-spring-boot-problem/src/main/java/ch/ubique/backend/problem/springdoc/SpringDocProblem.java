package ch.ubique.backend.problem.springdoc;

import ch.ubique.backend.problem.UbiqueThrowableProblem;
import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.annotation.Nullable;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;

/**
 * This interface only exists for springdoc documentation purposes, do not use it in actual code.
 *
 * <p>This extends the specification for RFC-9457 problem details with ubique-specific extensions:
 * `errorType` and `errorCode`
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC-9457</a>
 * @param <E>
 */
@Schema(
        description =
                """
                        Base documentation interface for all RFC-9457 problems.
                        Each (`errorCode`) has to represent an application-wide 1:1 mapping to (`type`, `errorType`).
                        Meaning, `errorCode` is unique application-wide, but `errorType` will only be unique for this problem `type`""")
@SuppressWarnings("unused")
public interface SpringDocProblem<
                E extends Enum<E> & ProblemErrorType,
                P extends AbstractThrowableProblem & UbiqueThrowableProblem<E>>
        extends Problem {

    @Nullable
    @JsonProperty
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorCode for this specific Problem. Non-null iff errorCode is set")
    E errorType();

    @Nullable
    @JsonProperty
    @Schema(
            description =
                    "Corresponds 1:1 to an equivalent errorType for this specific Problem. Non-null iff errorType is set")
    Integer errorCode();
}
