package ch.ubique.uetlib.spring.boot.geo.springdoc.autoconfigure;

import ch.ubique.uetlib.spring.boot.geo.springdoc.UetlibSpringBootGeoSpringDocConfig;
import ch.ubique.uetlib.spring.boot.springdoc.autoconfigure.UetlibSpringBootSpringDocAutoConfiguration;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = UetlibSpringBootSpringDocAutoConfiguration.class)
public class UetlibSpringBootGeoSpringDocAutoConfiguration {

    @Bean
    GlobalOpenApiCustomizer uetlibGeoSpringDocOpenApiCustomizer() {
        return UetlibSpringBootGeoSpringDocConfig.setDefaultConfig();
    }
}
