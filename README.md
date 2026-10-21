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

This logging policy is separate from `spring.mvc.log-resolved-exception`, which controls Spring's
own resolver WARN logging. That property does not disable uetlib's logging.
