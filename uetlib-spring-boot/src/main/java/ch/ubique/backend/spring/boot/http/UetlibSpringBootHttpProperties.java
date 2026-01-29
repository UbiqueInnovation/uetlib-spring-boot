package ch.ubique.backend.spring.boot.http;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration properties for http client configuration . */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.boot.http")
@Validated
public record UetlibSpringBootHttpProperties(
        @Schema(
                        description =
                                "The default user-agent to apply to all auto-configured RestClient"
                                    + " and RestTemplate")
                @NotNull
                String defaultUserAgent) {}
