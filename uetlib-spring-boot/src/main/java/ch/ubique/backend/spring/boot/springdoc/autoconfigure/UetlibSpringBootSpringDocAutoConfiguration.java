package ch.ubique.backend.spring.boot.springdoc.autoconfigure;

import ch.ubique.backend.spring.boot.springdoc.UetlibSpringBootSpringDocConfig;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class UetlibSpringBootSpringDocAutoConfiguration {

    @Bean
    GlobalOpenApiCustomizer uetlibSpringDocOpenApiCustomizer() {
        return UetlibSpringBootSpringDocConfig.setDefaultConfig();
    }
}
