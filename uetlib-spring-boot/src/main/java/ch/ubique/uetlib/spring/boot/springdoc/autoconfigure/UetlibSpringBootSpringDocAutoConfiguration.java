package ch.ubique.uetlib.spring.boot.springdoc.autoconfigure;

import ch.ubique.uetlib.spring.boot.springdoc.LocalizedValueModelConverter;
import ch.ubique.uetlib.spring.boot.springdoc.UetlibSpringBootSpringDocConfig;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SpringDocConfiguration.class)
public class UetlibSpringBootSpringDocAutoConfiguration {

    @Bean
    GlobalOpenApiCustomizer uetlibSpringDocOpenApiCustomizer() {
        return UetlibSpringBootSpringDocConfig.setDefaultConfig();
    }

    @Bean
    @ConditionalOnBean(ObjectMapperProvider.class)
    @ConditionalOnMissingBean
    LocalizedValueModelConverter localizedValueModelConverter(
            final ObjectMapperProvider objectMapperProvider) {

        return new LocalizedValueModelConverter(objectMapperProvider);
    }
}
