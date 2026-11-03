package ch.ubique.uetlib.spring.boot.validation;

import jakarta.validation.Validator;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for response validation, enabling properties and defining the validation advice.
 */
@AutoConfiguration
@EnableConfigurationProperties(ResponseValidationProperties.class)
public class UetlibSpringBootValidationResponseValidationAutoConfiguration {

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
            final Validator validator, final ResponseValidationProperties properties) {
        return new ResponseValidationAdvice(validator, properties);
    }
}
