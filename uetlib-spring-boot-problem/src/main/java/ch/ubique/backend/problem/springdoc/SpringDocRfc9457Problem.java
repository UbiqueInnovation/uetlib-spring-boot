package ch.ubique.backend.problem.springdoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import org.zalando.problem.Problem;

import java.net.URI;

/**
 * This interface only exists for springdoc documentation purposes, do not use it in actual code.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC-9457</a>
 */
@Schema(
        description =
                """
                        Base documentation interface for all Ubique problems.
                        Each (`errorCode`) has to represent an application-wide 1:1 mapping to (`type`, `errorType`).
                        Meaning, `errorCode` is unique application-wide, but `errorType` will only be unique for this problem `type`""")
@SuppressWarnings("unused")
public interface SpringDocRfc9457Problem extends Problem {

    @JsonProperty
    @Schema(example = "about:blank")
    URI type();

    /**
     * This is just for Springdoc to show a required `int` for `status` instead of the optional
     * `StatusType` model
     */
    @JsonProperty(required = true)
    @Schema(example = "400")
    int status();
}
