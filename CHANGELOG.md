# CHANGELOG

## 4.0.0 (unreleased)

- Align Spring Boot helper, validation and security packages under `ch.ubique.uetlib.spring.boot.*`; keep configuration-property names unchanged.

### Breaking changes

- Move Java packages from `ch.ubique.backend` to `ch.ubique.uetlib`, including class-name resources. Existing Spring configuration property keys are unchanged.
- Remove the inherited Ubique Artifactory repository and deployment destination; private consumers must configure their own registry.
- Add Apache-2.0 licensing for original code, preserve third-party licenses, and add public project metadata and contribution/security guidance.

- Move Maven coordinates from `ch.ubique:uetlib-*` to `ch.ubique.uetlib:uetlib-*`.
- Align uetlib parent, BOM and library references with the migration releases.

### Migration

After the migration releases and `uetlib-rewrite:0.1.0` are published, run this from the consuming project, then review the diff and run its tests:

```sh
mvn org.openrewrite.maven:rewrite-maven-plugin:6.46.1:run \
  -Drewrite.recipeArtifactCoordinates=ch.ubique.uetlib:uetlib-rewrite:0.1.0 \
  -Drewrite.activeRecipes=ch.ubique.uetlib.MigrateToPublicUetlib
```

The recipe updates Maven coordinates/versions, Java references and known class-name resources. Review application-owned packages under the old namespace, Kotlin, reflection patterns, native-access module flags and persisted class names separately. Use `:dryRun` instead of `:run` to preview a patch.


## 3.3.1

- `getLocalizedProblemField` used `localizedTitleArgs` also for localized detail field in problem response

## 3.3.0

- Include HTTP method, path, status, and exception type consistently in exception logs; use
  concise `Class#method` handler names for method-argument validation.

- Route method-argument validation summaries through the shared `logException` hook so projects
  can replace or suppress all handler logging. Document logging for project-specific handlers.

- Log exceptions reaching `UetlibExceptionHandler`: handled client errors at WARN and server errors
  at ERROR, including typed problems and extension advice traits. Include stack traces except for
  validation exceptions, whose messages can expose rejected values; preserve safe validation summaries.
- Log unmatched exceptions after MVC advice resolution without changing their response handling. Add an overridable
  `logException` hook for applications to customize the logging policy.

## 3.2.1

- Move `EnableConfigurationProperties` for `UetlibSpringBootProblemProperties` to new
  `UetlibSpringBootProblemAutoConfiguration`

## 3.2.0

- Log method argument violations on either `INFO` or `WARN` based on the value of
  `ch.ubique.backend.spring.boot.problem.include-constraint-violations` (`true` -> `WARN`)
- Version updates

## 3.1.0

- Add `retryable` (nullable, default null) in `AbstractThrowableProblem`

## 3.0.5

- latest uetlib parent
- version updates

## 3.0.4

- Include `org.zalando:logbook-httpclient5`, `org.springframework.boot:spring-boot-starter-logging`, and
  `org.springframework.boot:spring-boot-starter-opentelemetry` without
  `io.micrometer:micrometer-registry-otlp`, and
- Update uetlib parent and versions
- Update actions

## 3.0.3

- updated actions to not run tests twice for sonar
- update uetlib version

## 3.0.2

- If `SpringDocConfig` (or any other `ObjectMapperProvider` bean) is not loaded, `UetlibSpringBootAutoConfiguration`
  will not create a `localizedValueModelConverter` Bean.

## 3.0.1

- Remove the logbook body filter by the content-encoding header as this is mostly incorrect anyway, as content-types
  like application-json can be encoded but still logged in plain. Rely on content-type only. The following properties
  have no effect any more and can be removed.
  ```diff
  - ch.ubique.backend.spring.boot.logbook.response-filter.body-replacers.content-encodings=...
  ```

## 3.0.0

### Changes / Improvements

* Replaced the `SpringDocConfig` setup with Spring Boot auto-configuration for SpringDoc.
* SpringDoc defaults are now configured automatically when the dependency is present; explicit static initialization is
  no longer required.
* Added automatic SpringDoc/OpenAPI configuration for JTS geometry types.
* JTS geometry models are now documented automatically as GeoJSON schemas when the geo SpringDoc auto-configuration is
  active.

### Migration

If `SpringDocConfig` was used only as follows:

```java
static {
    SpringDocConfig.setDefaultConfig();
}
```

delete the entire static block. No replacement is required because the default SpringDoc configuration is now applied
automatically.

Additional schemas can still be registered manually:

```java
import static ch.ubique.backend.spring.boot.springdoc.UetlibSpringBootSpringDocConfig.ref;

@Configuration
class MySpringDocConfiguration {

    static {
        SpringDocUtils.getConfig()
                .replaceWithSchema(MyType.class, ref("MyType"));
    }

    @Bean
    GlobalOpenApiCustomizer myOpenApiCustomizer() {
        return openApi -> openApi.getComponents()
                .addSchemas("MyType", createMyTypeSchema());
    }

    private static Schema<?> createMyTypeSchema() {
        return new ObjectSchema()
                .addProperty("value", new StringSchema());
    }
}
```

## 2.6.2

- Minor Changes to the throwable problem for easier usage

## 2.6.1

- Allow multiple authorization header prefixes in `ApiKeyAuthenticationFilter`

## 2.6.0

- New builder style for `MoreJwtDecoders`
- Deprecate old overloaded methods for removal in `3.0.0`

## 2.5.0

### Improvements

- Also handle `ResourceNotFoundException` as a `404 Not Found` in `UetlibExceptionHandler`

## 2.4.0

### Improvements

- Add more content types to default logbook by replacers (
  `ch.ubique.backend.spring.boot.logbook.response-filter.body-replacers.content-types`). Now includes all of:
    - `text/html`
    - `text/css`
    - `text/javascript`
    - `font/*`
    - `image/*`
    - `application/protobuf`
- New `ApiKeyAuthenticationFilter` in `uetlib-spring-boot-security` to extend Spring Security's
  `AbstractPreAuthenticatedProcessingFilter` and extract headers. It defaults to the `Authorization` header where it
  expects the value format `ApiKey {key}`. It provides constructors to customize the header name (e.g., `X-API-KEY`),
  and optionally a custom prefix format (or
  `null`/empty for raw value extraction). This effectively replaces `ApiKeyAuthenticationFilter` in
  `uetlib-spring-boot` (will be removed in `3.0.0`).
- Introduce `ApiKeyAuthenticationManager` which handles timing-safe verification of API keys and can be initialized with
  a single key/authority, or a `Map<String, ? extends Collection<? extends GrantedAuthority>>` for multi-key support.

  **Migration Path:**
  If you want to use the library's `ApiKeyAuthenticationFilter` (which defaults to extracting the `Authorization`
  header, but can be configured for other headers like `X-API-KEY`), register it in your `SecurityFilterChain` paired
  with `ApiKeyAuthenticationManager`:

  ```java
  @Bean
  SecurityFilterChain apiFilterChain(final HttpSecurity http, @Value("${my-app.api-key}") final String apiKey) {
    final var manager = new ApiKeyAuthenticationManager(apiKey, "ROLE_API_USER");
    final var filter = new ApiKeyAuthenticationFilter(manager, "X-API-KEY");

    http
        .securityMatcher("/v1/demo/**")
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .anyRequest().hasRole("API_USER")
        );

    return http.build();
  }
  ```

## 2.3.0

### Improvements

- Allow setting no `audience` to validate in `MoreJwtValidator::issuerAndAudienceValidatingJwtDecoder`

## 2.2.2

### Bugfixes

- Fix NPEs in `RestClientExceptionAdviceTrait` and `MoreExceptionsExceptionAdviceTrait`

## 2.2.1

### Improvements

- Always add `x-problem-code` header for every `AbstractThrowableProblem` error responses.

## 2.2.0

### Breaking Changes

- Remove classes that should not exist here and also exist in `uetlib-core`. Use teh classes from `uetlib-core` instead:
    - `ByteFile`
    - `DataAndInput`
    - `InputCollectionLoaderService`
    - `InputCollectionLoaderServiceMapper`
    - `InputDataLoaderService`
    - `InputDataLoaderServiceMapper`

### Improvements

- Add `ProblemResponseHeadersProvider` interface: implement it on any `AbstractThrowableProblem` subclass to contribute
  custom HTTP response headers when that exception is handled.
- Add `customizeAbstractThrowableProblemHeaders` extension hook on `UetlibExceptionHandler`: override this method in
  your exception handler subclass to add or modify response headers for any `AbstractThrowableProblem`, regardless of
  its type.

## 2.1.1

- expose `errorType` from `AbstractThrowableProblem` again

## 2.1.0

### Breaking Changes

- merge module `uetlib-spring-boot-validation` into `uetlib-spring-boot` and remove the old module. Necessary Changes
  are either:
    - Replace module `uetlib-spring-boot-validation` with `uetlib-spring-boot` in your depencies if the latter is not
      yet included or
    - Drop the dependency on `uetlib-spring-boot-validation` in your project if `uetlib-spring-boot` is already
      included.

### Improvements

- Constraint violations from `ResponseValidationException`, `ConstraintViolationException`, `BindException`, and
  `MethodArgumentNotValidException` are now included as structured `violations` in the problem detail response body when
  `ch.ubique.backend.spring.boot.problem.include-constraint-violations=true`. This property defaults to `false` and
  should remain disabled in production environments to avoid leaking internal validation details.

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
    - Make custom problem extend `ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem` instead of zalando's
      `AbstractThrowableProblem`.
    - To document a response that returns a generic `ProblemDetail`, use
      `@Schema(implementation = ProblemDetail.class)` directly instead of
      `@Schema(implementation = SpringDocRfc9457Problem.class)` or similar.
    - Replace any `SpringDocConfig.setDefaultConfigWith()`, unless you have some custom overrides, as this applied via
      autoconfiguration anyway.
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
    - default user agent for all http clients (`RestClient` and `RestTemplate`) is now autoconfigured. You have to adapt
      (or add if you did not yet do this) your property for the default user-agent:
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

