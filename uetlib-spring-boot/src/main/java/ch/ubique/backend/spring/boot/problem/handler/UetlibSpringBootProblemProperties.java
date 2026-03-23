package ch.ubique.backend.spring.boot.problem.handler;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for http client configuration . */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.boot.problem")
@Validated
public final class UetlibSpringBootProblemProperties {
    @Schema(
            description =
                    "Whether to include constraint violations in the problem details response",
            defaultValue = "false")
    private boolean includeConstraintViolations;

    public boolean isIncludeConstraintViolations() {
        return includeConstraintViolations;
    }

    public void setIncludeConstraintViolations(final boolean includeConstraintViolations) {
        this.includeConstraintViolations = includeConstraintViolations;
    }
}
