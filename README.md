# Uetlib Spring Boot

Spring Boot specific helpers 

## Changelog

See [CHANGELOG.md](CHANGELOG.md)

## Exception logging

`UetlibExceptionHandler` logs handled exceptions at `WARN` for client errors and `ERROR` for
server errors, with the validation exception noted below. This includes Spring MVC exceptions, validation failures,
`AbstractThrowableProblem`, and advice traits that delegate to `handleExceptionInternal`
(including the Feign advice trait).

Unmatched exceptions are logged at `ERROR` and rethrown unchanged, so the servlet container and
security filters retain their existing handling. Exceptions resolved by another controller advice
or thrown outside Spring MVC are outside this handler's scope. The container may also log a
rethrown exception.

Applications with their own `@ExceptionHandler(Exception.class)` should override
`logUnhandledException(Exception, WebRequest)` instead, or remove their catch-all handler to use
this implementation. Declaring two handlers for `Exception` produces an ambiguous Spring MVC mapping.

Override `logException(Exception, HttpStatusCode, WebRequest)` to customize the logging policy.
Binding and constraint violations use safe summaries instead of logging the throwable, whose
message can contain rejected input values. Method-argument validation retains its existing safe
method/field summary at INFO or WARN according to `include-constraint-violations`.

Logs consistently include `status`, HTTP `method`, `path` (without the query string), and
`exception`. Non-validation logs also include the exception `message` and attach the throwable
for its stack trace. The status is `unknown` for unmatched exceptions. Method-argument validation also
includes `handler=Class#method` and `violations=[field: message]`, for example:

```text
Request failed: status=400 method=POST path=/users exception=MethodArgumentNotValidException handler=UserController#create violations=[username: must not be blank]
```

Validation logs omit the throwable and rejected-value fields. Custom validation messages can
still include rejected values, so avoid interpolating sensitive input in those messages.

### Project-specific handlers

Extend `UetlibExceptionHandler` and delegate custom handlers to `handleExceptionInternal()`
for shared logging and response handling. Do not also call `logException()` on that path:

```java
@ExceptionHandler(ProjectException.class)
ResponseEntity<Object> handleProjectException(ProjectException ex, WebRequest request) {
    return handleExceptionInternal(
            ex,
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()),
            new HttpHeaders(),
            HttpStatus.CONFLICT,
            request);
}
```

The response can be null if the response has already been committed; use your project's
nullable annotation on the method when required. If a handler builds its response directly,
call `logException(ex, status, request)` once instead. The typed `AbstractThrowableProblem`
handler follows this pattern to preserve its `ResponseEntity<ProblemDetail>` return type.

Override `logException()` to replace or suppress logging for all these paths, including the
method-argument validation summary. Call `super.logException(ex, status, request)` when you
want to retain the default policy for an exception. Unmatched exceptions still invoke the
hook directly with a null status before being rethrown.

To customize logging for one exception type, override the `Exception` signature and check its
runtime type, delegating other exceptions to the default policy:

```java
@Override
public void logException(
        Exception ex, @Nullable HttpStatusCode status, WebRequest request) {
    if (ex instanceof ProjectException projectException) {
        logger().info("Project request failed: {}", projectException.getMessage());
        return;
    }
    super.logException(ex, status, request);
}
```

Here `@Nullable` is `org.jspecify.annotations.Nullable`. Declaring only
`logException(ProjectException, HttpStatusCode, WebRequest)` adds an overload, not an override.
Java selects overloads using the argument's declared type: the shared handling path declares
the exception as `Exception`, so it will not select the `ProjectException` overload automatically.

This logging policy is separate from `spring.mvc.log-resolved-exception`, which controls Spring's
own resolver WARN logging. That property does not disable uetlib's logging.
