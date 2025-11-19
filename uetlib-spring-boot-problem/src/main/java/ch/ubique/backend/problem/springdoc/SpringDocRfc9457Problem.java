package ch.ubique.backend.problem.springdoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.annotation.Nullable;

import org.zalando.problem.Problem;

/**
 * This interface only exists for springdoc documentation purposes, do not use it in actual code.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC-9457</a>
 */
@Schema(description = "Base documentation interface for all RFC-9457 problems.")
@SuppressWarnings("unused")
public interface SpringDocRfc9457Problem extends Problem {
    @Nullable
    @JsonProperty
    Integer status();
}
