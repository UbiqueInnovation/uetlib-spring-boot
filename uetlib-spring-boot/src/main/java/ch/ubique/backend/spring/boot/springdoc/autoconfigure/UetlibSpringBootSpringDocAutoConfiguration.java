package ch.ubique.backend.spring.boot.springdoc.autoconfigure;

import ch.ubique.backend.core.springdoc.SpringDocConfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class UetlibSpringBootSpringDocAutoConfiguration {

    static {
        SpringDocConfig.setDefaultConfig();
    }
}
