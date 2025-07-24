package ch.ubique.backend.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@RestControllerAdvice
public class ResponseValidationAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseValidationAdvice.class);

    private final Validator validator;

    public ResponseValidationAdvice(final Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(
            MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Check if method or class is annotated with @ValidateResponse
        return returnType.hasMethodAnnotation(ValidateResponse.class)
                || returnType.getContainingClass().isAnnotationPresent(ValidateResponse.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        if (body != null) {
            final var start = Instant.now();
            final var violations = validator.validate(body);
            logger.debug(
                    "validation took: {}ms", Duration.between(start, Instant.now()).toMillis());
            if (!violations.isEmpty()) {
                throw new ResponseValidationException(violations, body.getClass());
            }
        }
        return body;
    }
}

