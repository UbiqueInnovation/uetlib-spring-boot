package ch.ubique.backend.spring.springdoc;

import ch.ubique.backend.core.springdoc.SpringDocConfig;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UetlibSpringBootSpringDocAutoConfiguration {

    static {
        SpringDocConfig.setDefaultConfig();
    }
}
