package ch.ubique.backend.spring.boot.springdoc.autoconfigure;

import ch.ubique.backend.core.springdoc.SpringDocConfig;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UetlibSpringBootSpringDocAutoConfiguration {

    static {
        SpringDocConfig.setDefaultConfig();
    }
}
