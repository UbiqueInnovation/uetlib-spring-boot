package ch.ubique.backend.spring.validation.response;

/**
 * Actions to take when response validation fails.
 * <p>
 * Configure via {@code ch.ubique.backend.spring.validation.response.fail.action}.
 * Default is {@link #DISABLE} for performance.
 * 
 * <h3>Configuration:</h3>
 * <pre>{@code
 * # application.properties
 * ch.ubique.backend.spring.validation.response.fail.action=THROW
 * }</pre>
 * 
 * @see ResponseValidationAdvice
 * @see ValidateResponse
 * 
 * @author Ubique Innovation AG
 * @since 1.4.0
 */
public enum ResponseValidationAction {
    
    /**
     * Log validation failures without throwing exceptions.
     * Uses configured log level from {@code ch.ubique.backend.spring.validation.response.fail.log-level}.
     */
    LOG,
    
    /**
     * Throw {@link ResponseValidationException} when validation fails.
     * Recommended for development and testing environments.
     */
    THROW,
    
    /**
     * Disable response validation completely.
     * Default setting for performance reasons.
     */
    DISABLE
}
