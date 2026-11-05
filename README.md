# Uetlib Spring Boot

Spring Boot specific helpers 

## Changelog

See [CHANGELOG.md](CHANGELOG.md)

## Exception logging

`UetlibExceptionHandler` logs handled exceptions at `WARN` for client errors and `ERROR` for
server errors, with the validation exception noted below. This includes Spring MVC exceptions, validation failures,
`AbstractThrowableProblem`, and advice traits that delegate to `handleExceptionInternal`
(including the Feign advice trait).

With problem auto-configuration enabled, a final MVC exception resolver logs unmatched exceptions
at `ERROR` after the standard MVC resolvers, including all controller advice. It leaves the exception
unresolved, so the servlet container and security filters retain their existing handling. Exceptions
resolved by another controller advice or thrown outside Spring MVC are outside this logging scope.
The container may also log an unresolved exception.

The resolver calls `logException(Exception, HttpStatusCode, WebRequest)` with a null status for
unmatched exceptions. Override that hook to customize their logging. If multiple
`UetlibExceptionHandler` advice beans exist, the resolver uses the highest-priority one.

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
hook with a null status before leaving the exception unresolved.

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
## Geo integration

Use `ch.uetlib:uetlib-geo-spring-boot` from the geo repository for geospatial JSON and Springdoc integration.
The former `uetlib-spring-boot-geo` module has moved there, under `ch.uetlib.geo.spring.boot`.
The base Spring Boot reactor no longer depends on geo.

## License

Except where otherwise noted, original uetlib code is licensed under the [Apache License 2.0](LICENSE). Third-party files retain their original license terms; see [NOTICE](NOTICE) and any third-party notices in this repository.
