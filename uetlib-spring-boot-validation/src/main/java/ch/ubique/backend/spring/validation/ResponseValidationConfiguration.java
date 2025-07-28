package ch.ubique.backend.spring.validation;

import jakarta.validation.Validator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for response validation, enabling properties and defining the validation advice.
 */
@Configuration
@EnableConfigurationProperties(ResponseValidationProperties.class)
public class ResponseValidationConfiguration {

    /**
     * Defines the ResponseValidationAdvice bean if not already present.
     *
     * @param validator the Jakarta Bean Validation validator
     * @param properties the response validation properties
     * @return the ResponseValidationAdvice instance
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseValidationAdvice responseValidationAdvice(
            Validator validator, ResponseValidationProperties properties) {
        return new ResponseValidationAdvice(validator, properties);
    }
}
