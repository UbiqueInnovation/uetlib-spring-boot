package ch.ubique.backend.spring.boot.http;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for http client configuration . */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.boot.http")
@Validated
public final class UetlibSpringBootHttpProperties {
    @Schema(
            description =
                    "The default user-agent to apply to all auto-configured RestClient"
                            + " and RestTemplate")
    private @NotNull String defaultUserAgent;

    public String getDefaultUserAgent() {
        return defaultUserAgent;
    }

    public void setDefaultUserAgent(final String defaultUserAgent) {
        this.defaultUserAgent = defaultUserAgent;
    }
}
