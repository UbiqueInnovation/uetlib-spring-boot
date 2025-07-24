# uetlib-spring-boot-validation

This module provides automatic response validation for Spring Boot REST controllers using Jakarta Bean Validation.

## Overview

The `uetlib-spring-boot-validation` module enables automatic validation of HTTP response bodies in Spring Boot applications. It uses Jakarta Bean Validation annotations to ensure that your API responses conform to the expected structure and constraints before being sent to clients.

## Features

- **Automatic Response Validation**: Validates response objects using Jakarta Bean Validation
- **Annotation-Based Configuration**: Simple `@ValidateResponse` annotation to enable validation
- **Performance Monitoring**: Logs validation execution time for performance monitoring
- **Detailed Error Information**: Provides comprehensive error details when validation fails

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ch.ubique</groupId>
    <artifactId>uetlib-spring-boot-validation</artifactId>
    <version>${uetlib.version}</version>
</dependency>
```

## Usage

### Basic Usage

1. **Annotate your controller methods or classes** with `@ValidateResponse`:

```java
@RestController
@ValidateResponse  // Enables validation for all methods in this controller
public class UserController {
    
    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
```

Or apply it to individual methods:

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    @ValidateResponse  // Enables validation for this method only
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
```

2. **Add validation annotations** to your response DTOs:

```java
public class UserResponse {
    
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    
    @Email
    private String email;
    
    @Min(18)
    private Integer age;
    
    // getters and setters...
}
```

### Error Handling

When validation fails, a `ResponseValidationException` is thrown. You can handle this exception using Spring's `@ExceptionHandler`:

```java
@RestControllerAdvice
public class ValidationExceptionHandler {
    
    @ExceptionHandler(ResponseValidationException.class)
    public ResponseEntity<ErrorResponse> handleResponseValidation(ResponseValidationException ex) {
        ErrorResponse error = new ErrorResponse(
            "Response validation failed for " + ex.getResponseType().getSimpleName(),
            ex.getViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

## Configuration

The module automatically configures itself when included in your Spring Boot application. It requires:
- A `Validator` bean (typically provided by Spring Boot's validation starter)
- Spring Web MVC (for the `ResponseBodyAdvice` functionality)

### Configuration Properties

You can customize the behavior of the validation module using the following application properties:

#### `ch.ubique.backend.spring.validation.response.fail.action`

Controls what action to take when response validation fails.

- **Default**: `DISABLE`
- **Possible values**:
  - `DISABLE`: Validation is completely disabled (default for performance reasons)
  - `LOG`: Log validation failures without throwing exceptions
  - `THROW`: Throw `ResponseValidationException` when validation fails


```properties
# application.properties
ch.ubique.backend.spring.validation.response.fail.action=THROW
```

#### `ch.ubique.backend.spring.validation.response.fail.log-level`

Controls the log level used when validation fails (only applicable when action is `LOG`).

- **Default**: `WARN`
- **Possible values**: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`


```properties
# application.properties
ch.ubique.backend.spring.validation.response.fail.action=LOG
ch.ubique.backend.spring.validation.response.fail.log-level=ERROR
```

### Configuration Examples

#### Development Environment (Strict Validation)
```properties
# application.properties
ch.ubique.backend.spring.validation.response.fail.action=THROW
```

#### Production Environment (Logging Only)
```properties
# application.properties
ch.ubique.backend.spring.validation.response.fail.action=LOG
ch.ubique.backend.spring.validation.response.fail.log-level=WARN
```

#### Performance-Critical Environment (Disabled)
```properties
# application.properties
ch.ubique.backend.spring.validation.response.fail.action=DISABLE
```

## Performance Considerations

- Validation is performed after the response object is created but before serialization
- Validation time is logged at DEBUG level for performance monitoring
- Consider the performance impact of complex validation rules on high-traffic endpoints

## Best Practices

1. **Use validation judiciously**: Only enable response validation where data integrity is critical
2. **Handle exceptions gracefully**: Implement proper exception handling for validation failures
3. **Monitor performance**: Use the logged validation times to identify performance bottlenecks
4. **Test thoroughly**: Ensure your validation rules work correctly with your response objects

## Dependencies

This module depends on:
- `uetlib-spring-boot` (core module)
- Jakarta Bean Validation API
- Spring Boot Web
- Spring Boot Validation Starter (for the Validator bean)