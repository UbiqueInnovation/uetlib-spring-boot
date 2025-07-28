package ch.ubique.backend.spring.validation;

import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for response validation. */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.validation.response.fail")
@Validated
public class ResponseValidationProperties {

    /**
     * The log level to use when logging validation failures (used when action is LOG). Default is
     * WARN.
     */
    private Level logLevel = Level.WARN;

    /** The action to take when response validation fails. Default is DISABLE for performance. */
    private ResponseValidationAction action = ResponseValidationAction.DISABLE;

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public ResponseValidationAction getAction() {
        return action;
    }

    public void setAction(ResponseValidationAction action) {
        this.action = action;
    }
}
