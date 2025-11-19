# CHANGELOG

# 1.6.0

## Improvements

- Add auto-configuration to `uetlib-spring-boot` for springdoc default config.
- Add auto-configuration to `uetlib-spring-boot-problem` for springdoc default config for problem related types.

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

# 1.5.3

## Improvements

- Add new `SpringDocRfc9457Problem` with all fields marked as optional and without `errorType` and `errorCode`.
- Mark `type`, `status`, `title`, `detail`, `errorType` and `errorCode` as required in `SpringDocProblem` and make it
  extend `SpringDocRfc9457Problem`.

## Breaking Changes

- Necessary changes to the SpringDocConfig:
  ```diff
    SpringDocConfig.setDefaultConfigWith(
                  customizer ->
  -                       customizer.replaceWithClass(Problem.class, SpringDocProblem.class));
  +                       customizer.replaceWithClass(Problem.class, SpringDocRfc9457Problem.class));
  ```

# 1.5.1

## Improvements

- Make `type` and `status` optional in `SpringDocProblem` in accordance with RFC-9457.
- Explicitly set `type`/`status`/`errorCode`/`errorType` as required in `UbiqueThrowableProblem`, as our explicit
  problems will always have these set.

# 1.4.11

## Improvements

- Fix `GMT` usage in header date formatter string in `HeaderUtility`
- Deprecate methods in `HeaderUtility` in favour of `HttpHeaderUtils` in `uetlib-core`

# 1.4.9

## Improvements

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

