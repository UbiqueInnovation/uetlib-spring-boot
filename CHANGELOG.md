# CHANGELOG

## 2.1.0

### Breaking Changes

- merge module `uetlib-spring-boot-validation` into `uetlib-spring-boot` and remove the old module. Necessary Changes are either:
  - Replace module `uetlib-spring-boot-validation` with `uetlib-spring-boot` in your depencies if the latter is not yet included or 
  - just drop the dependency on `uetlib-spring-boot-validation` in your project if `uetlib-spring-boot` is already included.

### Improvements

- Constraint violations from `ResponseValidationException`, `ConstraintViolationException`, `BindException`, and
  `MethodArgumentNotValidException` are now included as structured `violations` in the problem
  detail response body when `ch.ubique.backend.spring.boot.problem.include-constraint-violations=true`.
  This property defaults to `false` and should remain disabled in production environments to avoid
  leaking internal validation details.

## 2.0.5

- More Logbook autoconfiguration options for ResponseFilters

## 2.0.4

- Add user-agent interceptor before Logbook interceptor for `RestTemplate` to have the user-agent in the logs written by
  logbook.

## 2.0.2

### Improvements

- Add support for additional properties problem details when using `AbstractThrowableProblem`

## 2.0.0

### Improvements

- Spring Boot 4 compatibility
- Remove deprecated methods in `HeaderUtility`, use methods in `HttpHeaderUtils` in `uetlib-core`
- Add new module `uetlib-spring-boot-geo` with `JsonMapperBuilderCustomizers` for the different JTS module for
  `JsonMapper`

### Breaking Changes

- Merged module `uetlib-spring-boot-problem` into `uetlib-spring-boot` and removed the old module.
    - Removed dependency on `zalando/problem` and replaced it all with custom implementations
    - Replace these imports:
      ```diff
      - import ch.ubique.backend.problem.errortype.ProblemErrorType;
      + import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

      - import ch.ubique.backend.problem.springdoc.SpringDocProblem;
      + import ch.ubique.backend.spring.boot.problem.springdoc.SpringDocProblem;
      ```
    - Make custom problem extend `ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem` instead of
      zalando's `AbstractThrowableProblem`.
    - To document a response that returns a generic `ProblemDetail`, use
      `@Schema(implementation = ProblemDetail.class)` directly instead of
      `@Schema(implementation = SpringDocRfc9457Problem.class)` or similar.
    - Replace any `SpringDocConfig.setDefaultConfigWith()`, unless you have some custom overrides, as this applied
      via autoconfiguration anyway.
    - Remove **ANY** dependency on `zalando/problem*` in your project. All the functionality is now provided by
      `uetlib-spring-boot`.
    - Move all `MoreJwtValidators#*JwtDecoder(...)` methods to new class `MoreJwtDecoders` in
      `uetlib-spring-boot-security` and introduce new `MoreJwtValidators` methods with direct access to the same
      reusable JWT validators.
        - As a consequence, if you have an `at+jwt` decoder and you are using
          ```java
          private JwtDecoder connectorAtJwtDecoder() {
              return MoreJwtValidators.issuerAndAudienceValidatingJwtDecoder(
                      connectorIssuerUri,
                      List.of(connectorAudience),
                      // false, because we have type at+jwt tokens
                      customizer -> customizer.validateType(false));
          }
          ```
          then replace it with
          ```java
          private JwtDecoder connectorAtJwtDecoder(final RestTemplateBuilder restTemplateBuilder) {
              return MoreJwtDecoders.issuerAndAudienceValidatingAtJwtDecoder(
                      restTemplateBuilder.build(),
                      connectorIssuerUri,
                      List.of(connectorAudience));
          }
          ```
        - All `MoreJwtDecoders#*JwtDecoder(...)` methods now require a pre-configured `RestOperations` as the first
          argument. Most likely, this should be an instance of `restTemplateBuilder#build()`, where
          `restTemplateBuilder` is the Spring Boot autoconfigured `RestTemplateBuilder`.
    - default user agent for all http clients (`RestClient` and `RestTemplate`) is now autoconfigured. You have to
      adapt (or add if you did not yet do this) your property for the default user-agent:
      ```diff
      - ch.ubique.infobox.backend.external-api.default-user-agent=ub-infobox-ws-prod/${build-properties.version}
      + ch.ubique.backend.spring.boot.http.default-user-agent=ub-infobox-ws-prod/${build-properties.version}
      ```
      and you have to change all usages of `ch.ubique.infobox.backend.external-api.default-user-agent` to the new
      property.
    - New Logbook autoconfiguration. Remove any `WsLoggingConfig.java` or Logbook `Responsefilter`, `BodyFilters`, etc.
      from the `*BaseConfig` (unless they are very spcific, which they proably aren't) and remove a property in the form
      of:
      ```diff
      - ch.post.logbook.body-filter.enabled=false
      ```

## 1.7.1

### Bugfixes

- Fix an NPE in the autoconfigured springdoc open api customizer

## 1.7.0

### Improvements

- New auto-registered validation for `ResponseEntity<Body>`, so we can do validation on the generic type, e.g.:
  `ResponseEntity<@NotEmpty List<Foo>>` can now be validated.

## 1.6.3

### Improvements

- Add an optional backoff header option to `HeaderUtility` methods in `uetlib-spring-boot`.

# 1.6.0

### Improvements

- Add autoconfiguration to `uetlib-spring-boot` for springdoc default config.
- Add autoconfiguration to `uetlib-spring-boot-problem` for springdoc default config for problem related types.

## Breaking Changes

- Remove spring doc config in project base config. This is now both configured via the new auto configurations in
  `uetlib-spring-boot` and `uetlib-spring-boot-problem`:
  ```diff
  - static {
  -       SpringDocConfig.setDefaultConfig();
  - }
  ```
  or
  ```diff
  - static {
  -       SpringDocConfig.setDefaultConfigWith(
  -               customizer -> customizer.replaceWithClass(Problem.class, SpringDocProblem.class));
  - }
  ```

## 1.5.3

### Improvements

- Add new `SpringDocRfc9457Problem` with all fields marked as optional and without `errorType` and `errorCode`.
- Mark `type`, `status`, `title`, `detail`, `errorType` and `errorCode` as required in `SpringDocProblem` and make it
  extend `SpringDocRfc9457Problem`.

### Breaking Changes

- Necessary changes to the SpringDocConfig:
  ```diff
    SpringDocConfig.setDefaultConfigWith(
                  customizer ->
  -                       customizer.replaceWithClass(Problem.class, SpringDocProblem.class));
  +                       customizer.replaceWithClass(Problem.class, SpringDocRfc9457Problem.class));
  ```

## 1.5.1

### Improvements

- Make `type` and `status` optional in `SpringDocProblem` in accordance with RFC-9457.
- Explicitly set `type`/`status`/`errorCode`/`errorType` as required in `UbiqueThrowableProblem`, as our explicit
  problems will always have these set.

## 1.4.11

### Improvements

- Fix `GMT` usage in header date formatter string in `HeaderUtility`
- Deprecate methods in `HeaderUtility` in favour of `HttpHeaderUtils` in `uetlib-core`

## 1.4.9

### Improvements

- Use timing-attack-proof api key comparison for `ApiKeyAuthenticationFilter`

## 1.4.8

### New Features

#### uetlib-spring-boot-security

- add `ResourceIndicatorClientCredentialsRequestConverter` for RFC-8707 support

## 1.4.7

### New Features

#### uetlib-spring-boot-security

- Add new `MoreJwtValidators` with `MoreJwtValidators#issuerAndAudienceValidatingJwtDecoder(String, List<String>)` that
  validates the issuer and allowed audiences of a JWT.

## 1.4.5

### New Features

#### uetlib-spring-boot

- Moved `VersionToStringConverter` and `StringToVersionConverter` from `infobox-model` to `uetlib-spring-boot`.

## 1.4.4

### Improvements

#### uetlib-spring-boot-validation

- Add missing `Autoconfiguration.imports` file in `META-INF` for the auto configuration to actually work.

## 1.4.0

### New Features

#### uetlib-spring-boot-validation

- New `uetlib-spring-boot-validation` module for automatic response validation using Jakarta Bean Validation
    - Provides `@ValidateResponse` annotation to enable validation on controller methods or classes
    - Automatic validation of HTTP response bodies before serialization
    - Performance monitoring with validation timing logs
    - Comprehensive error handling with `ResponseValidationException`

## 1.3.0

### Improvements

- New `RestClientUtils` with `RestClientUtils#getQueryParamsFromRecord(Record)` to convert all the fields from a record
  to `MultiValueMap<String, String>` since the Spring Boot RestClient is annoying and does not provide a method to pass
  a class/record as `queryParams` directly and adding all parameters individually is error-prone as new fields get
  added.

## 1.2.1

### Improvements

- Latest uetlib dependencies with Spring Boot 3.5 support

## 1.2.0

- Upgrade uetlib-parent to 1.2.0. for Spring Boot 3.5

## 1.1.0

- Spring Boot 3.4

## 1.0.0

First release. The code was moved
from [UbiqueInnovation/backend-jvm-lib](https://github.com/UbiqueInnovation/backend-jvm-lib).

