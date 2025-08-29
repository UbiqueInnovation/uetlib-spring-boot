package ch.ubique.backend.spring.validation;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for response validation. */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.validation.response.fail")
@Validated
public record ResponseValidationProperties(
        @Schema(
                        description =
                                "The log level to use when logging validation failures (used when action is LOG). Default is WARN.",
                        defaultValue = "WARN")
                @DefaultValue("WARN")
                @NotNull
                Level logLevel,
        @Schema(
                        description =
                                "The action to take when response validation fails. Default is DISABLE for performance.",
                        defaultValue = "DISABLE")
                @DefaultValue("DISABLE")
                @NotNull
                ResponseValidationAction action) {}
