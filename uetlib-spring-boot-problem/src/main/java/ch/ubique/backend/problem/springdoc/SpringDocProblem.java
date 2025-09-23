package ch.ubique.backend.problem.springdoc;

import ch.ubique.backend.problem.UbiqueThrowableProblem;
import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;

import java.net.URI;

/**
 * This interface only exists for springdoc documentation purposes, do not use it in actual code
 *
 * @param <E>
 */
@Schema(
        description =
                """
                        Base documentation interface for all Ubique problems.
                        Each (`errorCode`) has to represent an application-wide 1:1 mapping to (`type`, `errorType`).
                        Meaning, `errorCode` is unique application-wide, but `errorType` will only be unique for this `type`/problem""")
@SuppressWarnings("unused")
public interface SpringDocProblem<
                E extends Enum<E> & ProblemErrorType,
                P extends AbstractThrowableProblem & UbiqueThrowableProblem<E>>
        extends Problem {

    @NotNull
    @JsonProperty(required = true)
    URI type();

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

    /**
     * This is just for Springdoc to show a required `int` for `status` instead of the optional
     * `StatusType` model
     */
    @NotNull
    @JsonProperty(required = true)
    @Schema(example = "400")
    int status();
}
