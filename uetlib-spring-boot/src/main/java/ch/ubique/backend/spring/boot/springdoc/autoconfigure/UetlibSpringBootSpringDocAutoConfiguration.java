package ch.ubique.backend.spring.boot.springdoc.autoconfigure;

import ch.ubique.backend.spring.boot.springdoc.LocalizedValueModelConverter;
import ch.ubique.backend.spring.boot.springdoc.UetlibSpringBootSpringDocConfig;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class UetlibSpringBootSpringDocAutoConfiguration {

    @Bean
    GlobalOpenApiCustomizer uetlibSpringDocOpenApiCustomizer() {
        return UetlibSpringBootSpringDocConfig.setDefaultConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    LocalizedValueModelConverter localizedValueModelConverter(
            final ObjectMapperProvider objectMapperProvider) {

        return new LocalizedValueModelConverter(objectMapperProvider);
    }
}
