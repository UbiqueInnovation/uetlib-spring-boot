package ch.uetlib.spring.boot.validation;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for response validation. */
@ConfigurationProperties(prefix = "ch.uetlib.spring.boot.validation.response.fail")
@Validated
public final class ResponseValidationProperties {
    @Schema(
            description =
                    "The log level to use when logging validation failures (used when"
                            + " action is LOG). Default is WARN.",
            defaultValue = "WARN")
    private @NotNull Level logLevel = Level.WARN;

    @Schema(
            description =
                    "The action to take when response validation fails. Default is"
                            + " DISABLE for performance.",
            defaultValue = "DISABLE")
    private @NotNull ResponseValidationAction action = ResponseValidationAction.DISABLE;

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(final Level logLevel) {
        this.logLevel = logLevel;
    }

    public ResponseValidationAction getAction() {
        return action;
    }

    public void setAction(final ResponseValidationAction action) {
        this.action = action;
    }
}
